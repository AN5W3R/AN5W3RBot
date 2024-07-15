package org.example.llonebotbase;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.example.llonebotbase.entity.Message;
import org.example.llonebotbase.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息监听
 */
@ClientEndpoint
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Session session;
    public static Client instance;
    public static boolean isOpen = false;
 
    private Client(String url) {
        try {
            session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public synchronized static boolean connect(String url) {
        instance = new Client(url);
        return true;
    }
 
    @OnOpen
    public void onOpen(Session session) {
        isOpen = true;
        logger.info("连接成功！");
    }
 
    @OnClose
    public void onClose(Session session) {
        isOpen = false;
        logger.info("连接关闭！");
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("连接错误！");
    }

    //
    @OnMessage
    public void onMessage(String message) throws UnsupportedEncodingException {//message对象正在以json字符串的形式传输,这里要重构,让它以对象形式传输
        System.out.println("\n"+message);
//        if (message.contains("\"request_type\":\"friend\"")) {//信息类型为好友请求
//            Friend parseObject = JSONObject.parseObject(message, Friend.class);//JSON转换为对象
//
//            sendFriend(parseObject);
//        }
//
        if (message.contains("\"post_type\":\"message\"")) {//信息类型为消息
            Message parseObject = JSONObject.parseObject(message, Message.class);//JSON转换为对象

            if("private".equals(parseObject.getMessageType())){//私聊信息
                sendPrivateMsg(parseObject);
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq=3363590760]")){//被@的情况message
                    sendGroupMsg(parseObject);
                } //else if(parseObject.getMessage().contains("[CQ:at,qq=2044284028]")) {
//                    parseObject.setMessage("@2044284028");
//                    sendGroupMsg(parseObject);
//                } else if("2468794766".equals(parseObject.getUserId())){//群聊中某人说话message.contains("\"user_id\":2468794766")
//                    parseObject.setMessage("小莫");
//                    sendGroupMsg(parseObject);
//                }
            }
        }

    }
//
//    /**
//     * 好友请求
//     */
//    private synchronized void sendFriend(Friend parseObject) {
//        logger.info("收到好友请求：" + parseObject.getUser_id() + ",验证消息：" + parseObject.getComment());
//        Request<Object> paramsRequest = new Request<>();
//        paramsRequest.setAction("set_friend_add_request");
//        Map<String, Object> params = new HashMap<>();
//        params.put("flag", parseObject.getFlag());
//        if (parseObject.getComment().contains("哭来兮苦")) {
//            params.put("approve", true);
//            logger.info("已同意好友请求：" + parseObject.getUser_id());
//        } else {
//            params.put("approve", false);
//            logger.info("已拒绝好友请求：" + parseObject.getUser_id());
//        }
//        paramsRequest.setParams(params);
//        instance.session.getAsyncRemote().sendText(JSONObject.toJSONString(paramsRequest));
//
//    }
//
    /**
     * 好友消息
     */
    public synchronized static void sendPrivateMsg(Message parseObject) throws UnsupportedEncodingException {
        String message = parseObject.getRawMessage();
        logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_private_msg");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", parseObject.getUserId());
//        String ai = LocalChat.AiOne(message);

        String ai = LocalChat.ChatByMsg(message);
        if (ai == null) {
          ai = "宝，回复失败!重新试试把!";
        }
        params.put("message",ai);
        paramsRequest.setParams(params);

        String msg = JSONObject.toJSONString(paramsRequest);//发出信息
        instance.session.getAsyncRemote().sendText(msg);
    }

    /**
     * 群聊信息
     */
    public synchronized static void sendGroupMsg( Message parseObject) throws UnsupportedEncodingException {
        String message = parseObject.getRawMessage();
        logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_group_msg");
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", parseObject.getGroupId());
//        String ai = AiOne(message);

        String ai = LocalChat.ChatByMsg(message);

        if (ai == null) {
            ai = "宝，回复失败!重新试试把!";
        }
        params.put("message",ai);
        paramsRequest.setParams(params);

        String msg = JSONObject.toJSONString(paramsRequest);
        instance.session.getAsyncRemote().sendText(msg);
    }

}