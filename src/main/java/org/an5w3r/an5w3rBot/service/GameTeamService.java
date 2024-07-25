package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.Team;

import java.io.IOException;
import java.util.*;

public class GameTeamService {
    public static Map<String, Map<String,Team>> groupMap = new HashMap<>();
//    public static Map<String,Team> groupMap.get(message.getGroupId()) = new HashMap<>();

    static boolean existTeam(String teamName, String groupId){
        if (groupMap.containsKey(groupId)){
            return groupMap.get(groupId).containsKey(teamName);
        }
        groupMap.put(groupId,new HashMap<String, Team>());
        return false;
    }

    public static void addTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();
        //判断格式是否正确
        if (splitMsg.length<4){//格式不正确
            MsgAction.sendMsg(message,new MsgItem("指令格式不正确"));
            return;
        }
        //判断队伍是否存在
        if (existTeam(splitMsg[1],message.getGroupId())){//队伍已存在
            MsgAction.sendMsg(message,new MsgItem("队伍已存在"));
            return;
        }
        //新建队伍注入信息
        Team team = new Team();
        team.setName(splitMsg[1]);
        team.setMessage(message);
        Integer maxCount = Integer.valueOf(splitMsg[2]);
        if (maxCount>1){
            team.setMaxCount(maxCount);
        } else {
            MsgAction.sendMsg(message,new MsgItem("人数必须大于1人"));
            return;
        }


        team.setText(splitMsg[3]);
        List<Map<String,String>> senderList = new ArrayList<>();
        team.setSenderList(senderList);
        team.join(message);

        groupMap.get(message.getGroupId()).put(splitMsg[1],team);//加入群内队伍列表

        //提示创建成功
        MsgAction.sendMsg(message,new MsgItem("创建队伍成功\n"+team));
    }

    public static void joinTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length<2){
            MsgAction.sendMsg(message,new MsgItem("指令格式不正确"));
            return;
        }
        if (!existTeam(splitMsg[1],message.getGroupId())){
            MsgAction.sendMsg(message,new MsgItem("队伍不存在"));
            return;
        }
        //获取队伍
        Team team = groupMap.get(message.getGroupId()).get(splitMsg[1]);
        team.join(message);
//        groupMap.get(message.getGroupId()).put(splitMsg[1],team);//更新队伍

        MsgAction.sendMsg(message,new MsgItem(team.toString()));
        if (team.getSenderList().size()== team.getMaxCount()){
            playTeam(message);
        }
    }

    public static void leaveTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length<2){
            MsgAction.sendMsg(message,new MsgItem("指令格式不正确"));
            return;
        }
        if (!existTeam(splitMsg[1],message.getGroupId())){
            MsgAction.sendMsg(message,new MsgItem("队伍不存在"));
            return;
        }
        //获取队伍
        Team team = groupMap.get(message.getGroupId()).get(splitMsg[1]);
        team.leave(message);
//        groupMap.get(message.getGroupId()).put(splitMsg[1],team);//更新队伍

        MsgAction.sendMsg(message,new MsgItem(team.toString()));
        if (team.getSenderList().isEmpty()){
            removeTeam(message);
        }
    }

    public static void removeTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length<2){
            MsgAction.sendMsg(message,new MsgItem("指令格式不正确"));
            return;
        }
        if (!groupMap.get(message.getGroupId()).containsKey(splitMsg[1])){
            MsgAction.sendMsg(message,new MsgItem("队伍不存在"));
            return;
        }

        groupMap.get(message.getGroupId()).remove(splitMsg[1]);
        MsgAction.sendMsg(message,new MsgItem("队伍"+splitMsg[1]+"已解散"));
    }

    public static void playTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length<2){
            MsgAction.sendMsg(message,new MsgItem("指令格式不正确"));
            return;
        }
        if (!groupMap.get(message.getGroupId()).containsKey(splitMsg[1])){
            MsgAction.sendMsg(message,new MsgItem("队伍不存在"));
            return;
        }
        Team team = groupMap.get(message.getGroupId()).get(splitMsg[1]);
        team.go();

        groupMap.get(message.getGroupId()).remove(splitMsg[1]);
    }

    public static void promptTeam() throws IOException {//队伍提醒
        for (String s : groupMap.keySet()) {
            Map<String, Team> teamMap = groupMap.get(s);
            if (!teamMap.isEmpty()){
                Set<String> teamNames = teamMap.keySet();
                for (String teamName : teamNames) {
                    Team team = teamMap.get(teamName);
                    MsgAction.sendMsg(team.getMessage(),new MsgItem(team.toString()));
                }
            }
        }
    }
}

