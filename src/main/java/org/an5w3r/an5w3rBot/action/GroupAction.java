package org.an5w3r.an5w3rBot.action;

import com.alibaba.fastjson.JSONObject;
import org.an5w3r.an5w3rBot.Client;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GroupAction {
    private static final Logger logger = LoggerFactory.getLogger(MsgAction.class);

    public synchronized static void setGroupCard(Message message,String card){
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("set_group_card");
        Map<String,String> map = new HashMap<>();
        map.put("group_id",message.getGroupId());
        map.put("user_id",message.getUserId());
        map.put("card",card);
        paramsRequest.setParams(map);

        logger.info("更改群名片");
        String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求
    }

    public synchronized static void setGroupBan(Message message,int duration){
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("set_group_ban");
        Map<String,Object> map = new HashMap<>();
        map.put("group_id",message.getGroupId());
        map.put("user_id",message.getUserId());
        map.put("duration",duration);
        paramsRequest.setParams(map);

        logger.info("禁言");
        String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求
    }
}
