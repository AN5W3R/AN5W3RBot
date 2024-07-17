package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.service.MsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * 消息监听
 */
@ClientEndpoint
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public Session session;
    public static Client instance;
 
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
        Setting.isOpen = true;
        logger.info("连接成功！");
    }
 
    @OnClose
    public void onClose(Session session) {
        Setting.isOpen = false;
        logger.info("连接关闭！");
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("连接错误！");
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
//        System.out.println("\n"+message);
        if (message.contains("\"post_type\":\"message\"")) {//处理message信息
            Message parseObject = JSONObject.parseObject(message, Message.class);//JSON转换为对象

            //TODO 不能直接sendMsg,要添加判断适应更多情况
            if("private".equals(parseObject.getMessageType())){//私聊信息
                MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneText(message));
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq="+parseObject.getSelfId()+"]")){//被@的情况message
                    if(parseObject.getRawMessage().equals("[CQ:at,qq="+parseObject.getSelfId()+"] #涩图")){
                        MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneRandomImage("Random"));
                    }else {
                        MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneText(message));
                    }

                }
            }
        }
    }

}

