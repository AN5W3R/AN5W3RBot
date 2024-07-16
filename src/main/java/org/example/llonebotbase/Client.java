package org.example.llonebotbase;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.example.llonebotbase.entity.Message;
import org.example.llonebotbase.entity.MsgItem;
import org.example.llonebotbase.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
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
        if (message.contains("\"post_type\":\"message\"")) {//信息类型为消息
            Message parseObject = JSONObject.parseObject(message, Message.class);//JSON转换为对象

            if("private".equals(parseObject.getMessageType())){//私聊信息
                sendPrivateMsg(parseObject);
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq="+parseObject.getSelfId()+"]")){//被@的情况message
                    sendGroupMsg(parseObject);
                }
            }
        }
    }
    /**
     * 好友消息
     */
    //TODO 私聊和群聊合并一个函数 会话层
    public synchronized static void sendPrivateMsg(Message parseObject) throws UnsupportedEncodingException {
        String message = parseObject.getRawMessage();
        logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);

        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_msg");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", parseObject.getUserId());
        params.put("detail_type","private");

        String resMsg = LocalChat.ChatByMsg(message);
        if (resMsg == null) {
            resMsg = "出了点小问题...";
        }
        params.put("message",resMsg);
        paramsRequest.setParams(params);
        logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);

        String msg = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        instance.session.getAsyncRemote().sendText(msg);//发出请求
    }

    /**
     * 群聊信息
     */
    public synchronized static void sendGroupMsg( Message parseObject) throws UnsupportedEncodingException {
        String message = parseObject.getRawMessage();
        logger.info("收到群" + parseObject.getGroupId() + "的消息：" + message);

        Request<Object> paramsRequest = new Request<>();//用于发送请求的对象
        paramsRequest.setAction("send_msg");//设置发送消息
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", parseObject.getGroupId());//设置要发送的群
        params.put("detail_type","group");


//        params.put("message",resMsg);//应该放进data
        ArrayList<MsgItem> msgList = new ArrayList<>();

//TODO        这段可以封装返回MsgItem的函数，image和text等用同一个函数，做成构造函数?
        MsgItem item = new MsgItem();
        item.setType("text");
        Map<String, String> data  = new HashMap<>();
        String resMsg = LocalChat.ChatByMsg(message);//
        if (resMsg == null) {
            resMsg = "出了点小问题...";
        }
        data.put("text",resMsg);
        item.setData(data);

        msgList.add(item);

        MsgItem item1 = new MsgItem();
        item1.setType("image");
        Map<String, String> data1  = new HashMap<>();
        data1.put("file","https://gchat.qpic.cn/gchatpic_new/1542338612/758025242-3108042462-E04E0D37BB15FF418729C4AE9118A180/0?term=255&is_origin=0");
        item1.setData(data1);
        msgList.add(item1);

        params.put("message",msgList);
        paramsRequest.setParams(params);

        String msg = JSONObject.toJSONString(paramsRequest);
        System.out.println(msg);
        instance.session.getAsyncRemote().sendText(msg);
    }

}

