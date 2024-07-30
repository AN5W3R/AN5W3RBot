package org.an5w3r.an5w3rBot.action;

import com.alibaba.fastjson.JSONObject;
import org.an5w3r.an5w3rBot.Client;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MsgAction {
    private static final Logger logger = LoggerFactory.getLogger(MsgAction.class);

    //发送消息synchronized
    public static void sendMsg(Message message, MsgItem... retMessage) throws IOException {
        List<MsgItem> itemList = new ArrayList<>(Arrays.asList(retMessage));
        sendMsg(message,itemList);
    }

    public synchronized static void sendMsg(Message message, List<MsgItem> itemList) {
        String rawMessage = message.getRawMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("detail_type",message.getMessageType());
        if("private".equals(message.getMessageType())){
            logger.info("\n向"+message.getUserId()+"发出消息"+itemList+"\n");
            params.put("user_id", message.getUserId());
        } else if("group".equals(message.getMessageType())){
            logger.info("\n向群"+message.getGroupId()+"发出消息"+itemList+"\n");
            params.put("group_id", message.getGroupId());
        } else {
            return;
        }
        //这里要使用不同的方法来决定不同的发生内容
        params.put("message",itemList);


        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_msg");
        paramsRequest.setParams(params);


        String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求
    }
    //撤回消息
    public synchronized static void deleteMsg(Message message){
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("delete_msg");
        Map<String,String> map = new HashMap<>();
        map.put("message_id",message.getMessageId());
        paramsRequest.setParams(map);

        logger.info("撤回消息");
        String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求
    }
}
