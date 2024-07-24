package org.an5w3r.an5w3rBot.service;



import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameTeamService {
    public static Map<String, Map<String,Team>> groupMap = new HashMap<>();
//    public static Map<String,Team> groupMap.get(parseObject.getGroupId()) = new HashMap<>();

    static boolean existTeam(String teamName, String groupId){
        if (groupMap.containsKey(groupId)){
            return groupMap.get(groupId).containsKey(teamName);
        }
        groupMap.put(groupId,new HashMap<String, Team>());
        return false;
    }

    public static void addTeam(Message parseObject, String[] msgStr) throws IOException {
        //判断格式是否正确
        if (msgStr.length<4){//格式不正确
            MsgAction.sendMsg(parseObject,new MsgItem("指令格式不正确"));
            return;
        }
        //判断队伍是否存在
        if (existTeam(msgStr[1],parseObject.getGroupId())){//队伍已存在
            MsgAction.sendMsg(parseObject,new MsgItem("队伍已存在"));
            return;
        }
        //注入信息

        Team team = new Team();
        team.setName(msgStr[1]);
        team.setParseObject(parseObject);
        team.setMaxCount(Integer.valueOf(msgStr[2]));
        team.setText(msgStr[3]);
        List<Map<String,String>> senderList = new ArrayList<>();
        team.setSenderList(senderList);
        team.join(parseObject);

        groupMap.get(parseObject.getGroupId()).put(msgStr[1],team);//更新群内队伍列表
//        groupMap.put(parseObject.getGroupId(),groupMap.get(parseObject.getGroupId()));//更新群列表

        //提示创建成功
        MsgAction.sendMsg(parseObject,new MsgItem("创建队伍成功\n"+team));
    }

    public static void joinTeam(Message parseObject, String[] msgStr) throws IOException {
        if (msgStr.length<2){
            MsgAction.sendMsg(parseObject,new MsgItem("指令格式不正确"));
            return;
        }
        if (!existTeam(msgStr[1],parseObject.getGroupId())){
            MsgAction.sendMsg(parseObject,new MsgItem("队伍不存在"));
            return;
        }
        Team team = groupMap.get(parseObject.getGroupId()).get(msgStr[1]);


        team.join(parseObject);
        groupMap.get(parseObject.getGroupId()).put(msgStr[1],team);//更新队伍
//        groupMap.put(parseObject.getGroupId(),groupMap.get(parseObject.getGroupId()));//更新群列表

        MsgAction.sendMsg(parseObject,new MsgItem(team.toString()));
        if (team.getSenderList().size()== team.getMaxCount()){
            playTeam(parseObject,msgStr);
        }
    }

    public static void leaveTeam(Message parseObject, String[] msgStr) throws IOException {
        if (msgStr.length<2){
            MsgAction.sendMsg(parseObject,new MsgItem("指令格式不正确"));
            return;
        }
        if (!existTeam(msgStr[1],parseObject.getGroupId())){
            MsgAction.sendMsg(parseObject,new MsgItem("队伍不存在"));
            return;
        }
        Team team = groupMap.get(parseObject.getGroupId()).get(msgStr[1]);
        team.leave(parseObject);
        groupMap.get(parseObject.getGroupId()).put(msgStr[1],team);//更新队伍
//        groupMap.put(parseObject.getGroupId(),groupMap.get(parseObject.getGroupId()));//更新群列表

        MsgAction.sendMsg(parseObject,new MsgItem(team.toString()));
        if (team.getSenderList().isEmpty()){
            removeTeam(parseObject, msgStr);
        }
    }

    public static void removeTeam(Message parseObject, String[] msgStr) throws IOException {
        if (msgStr.length<2){
            MsgAction.sendMsg(parseObject,new MsgItem("指令格式不正确"));
            return;
        }
        if (!groupMap.get(parseObject.getGroupId()).containsKey(msgStr[1])){
            MsgAction.sendMsg(parseObject,new MsgItem("队伍不存在"));
            return;
        }
        groupMap.get(parseObject.getGroupId()).remove(msgStr[1]);
        MsgAction.sendMsg(parseObject,new MsgItem("队伍"+msgStr[1]+"已解散"));
    }

    public static void playTeam(Message parseObject, String[] msgStr) throws IOException {
        if (msgStr.length<2){
            MsgAction.sendMsg(parseObject,new MsgItem("指令格式不正确"));
            return;
        }
        if (!groupMap.get(parseObject.getGroupId()).containsKey(msgStr[1])){
            MsgAction.sendMsg(parseObject,new MsgItem("队伍不存在"));
            return;
        }
        Team team = groupMap.get(parseObject.getGroupId()).get(msgStr[1]);
        team.go();

        groupMap.get(parseObject.getGroupId()).remove(msgStr[1]);
    }
}

