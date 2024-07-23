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

    //发送消息
    public synchronized static void sendMsg(Message parseObject, MsgItem... retMessage) throws IOException {
//        String message = parseObject.getRawMessage();
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("detail_type",parseObject.getMessageType());
//        if("private".equals(parseObject.getMessageType())){
//            logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);
//            params.put("user_id", parseObject.getUserId());
//        } else if("group".equals(parseObject.getMessageType())){
//            logger.info("收到群" + parseObject.getGroupId() + "的消息：" + message);
//            params.put("group_id", parseObject.getGroupId());
//        } else {
//            return;
//        }
//        //这里要使用不同的方法来决定不同的发生内容
        List<MsgItem> itemList = new ArrayList<>();
        itemList.addAll(Arrays.asList(retMessage));
        sendMsg(parseObject,itemList);
//        params.put("message",messageList);
//
//
//        Request<Object> paramsRequest = new Request<>();
//        paramsRequest.setAction("send_msg");
//        paramsRequest.setParams(params);
//
//        logger.info("发出消息");
//        String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
//        Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求

    }

   public synchronized static void deleteMsg(Message parseObject){
       Request<Object> paramsRequest = new Request<>();
       paramsRequest.setAction("delete_msg");
       Map<String,String> map = new HashMap<>();
       map.put("message_id",parseObject.getMessageId());
       paramsRequest.setParams(map);

       logger.info("撤回消息");
       String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
       Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求
   }

    public static void sendMsg(Message parseObject, List<MsgItem> itemList) {
        String message = parseObject.getRawMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("detail_type",parseObject.getMessageType());
        if("private".equals(parseObject.getMessageType())){
            logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);
            params.put("user_id", parseObject.getUserId());
        } else if("group".equals(parseObject.getMessageType())){
            logger.info("收到群" + parseObject.getGroupId() + "的消息：" + message);
            params.put("group_id", parseObject.getGroupId());
        } else {
            return;
        }
        //这里要使用不同的方法来决定不同的发生内容
        params.put("message",itemList);


        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_msg");
        paramsRequest.setParams(params);

        logger.info("发出消息");
        String strRequest = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        Client.instance.session.getAsyncRemote().sendText(strRequest);//发出请求
    }
}
