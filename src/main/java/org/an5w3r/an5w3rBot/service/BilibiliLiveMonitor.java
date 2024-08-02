package org.an5w3r.an5w3rBot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.RoomInfo;
import org.an5w3r.an5w3rBot.util.JSONUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BilibiliLiveMonitor {
    private static final String API_URL = "https://api.live.bilibili.com/room/v1/Room/get_info?room_id=";
    private static final String STREAM_URL_API = "https://live.bilibili.com/";

    static Map<String, String[]> groupRoomMap;
    static Map<String, Boolean> liveStatusMap = new HashMap<>();

    static {//在这里读取群号和房间号信息
//        groupRoomMap.put("758025242", new String[]{"25452483", "76"});
        // Add more group-room mappings as needed
        try {
            groupRoomMap = JSONUtil.getBiliBiliStreamRoom();
            for (String[] roomIds : groupRoomMap.values()) {
                for (String roomId : roomIds) {
                    liveStatusMap.put(roomId, false);
                }
            }
        } catch (IOException e) {
        }

    }

    public static void liveMonitor() {
        try {
            for (String roomId : liveStatusMap.keySet()) {
                RoomInfo roomInfo = checkLiveStatus(roomId);
                if (roomInfo.isLive() && !liveStatusMap.get(roomId)) {
                    List<String> groupsToNotify = getGroupsForRoom(roomId);
                    for (String groupId : groupsToNotify) {
                        Message message = new Message();
                        message.setGroupId(groupId);
                        message.setMessageType("group");
                        List<MsgItem> itemList = new ArrayList<>();
                        itemList.add(new MsgItem(roomInfo.getTitle() + " 已开播！\n房间号:" + roomId));
                        itemList.add(new MsgItem("\n直播间地址: " + roomInfo.getStreamUrl()));
                        itemList.add(new MsgItem("\n分区: " + roomInfo.getAreaName()));
                        itemList.add(new MsgItem("image", "file", roomInfo.getUserCover()));
                        MsgAction.sendMsg(message, itemList);
                    }
                    liveStatusMap.put(roomId, true);
                } else if (!roomInfo.isLive() && liveStatusMap.get(roomId)) {
                    List<String> groupsToNotify = getGroupsForRoom(roomId);
                    for (String groupId : groupsToNotify) {
                        Message message = new Message();
                        message.setGroupId(groupId);
                        message.setMessageType("group");
                        List<MsgItem> itemList = new ArrayList<>();
                        itemList.add(new MsgItem("直播间 " + roomInfo.getTitle() + " 已下播" + roomId));
                        MsgAction.sendMsg(message, itemList);
                    }
                    liveStatusMap.put(roomId, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static RoomInfo checkLiveStatus(String roomId) throws Exception {
        URL url = new URL(API_URL + roomId);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 7890));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // 10 seconds connection timeout
        connection.setReadTimeout(10000); // 10 seconds read timeout
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            String response = content.toString();
            JSONObject jsonObject = JSON.parseObject(response);
            JSONObject data = jsonObject.getJSONObject("data");

            String areaName = data.getString("area_name");
            String userCover = data.getString("user_cover");
            String title = data.getString("title");
            String liveStatus = data.getString("live_status");
            String streamUrl = STREAM_URL_API + roomId;

            return new RoomInfo(areaName, userCover, title, "1".equals(liveStatus), streamUrl);
        } else {
            System.err.println("Server returned non-OK status: " + responseCode);
            return new RoomInfo("", "", "", false, "");
        }
    }
    private static List<String> getGroupsForRoom(String roomId) {
        List<String> groups = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : groupRoomMap.entrySet()) {
            for (String id : entry.getValue()) {
                if (id.equals(roomId)) {
                    groups.add(entry.getKey());
                    break;
                }
            }
        }
        return groups;
    }
}
