package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.service.MsgService;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    public void onMessage(String message) throws IOException, ExecutionException, InterruptedException {
        System.out.println("\n"+message);
        if (message.contains("\"post_type\":\"message\"")) {//处理message信息
            Message parseObject = JSONObject.parseObject(message, Message.class);//JSON转换为对象

            if("private".equals(parseObject.getMessageType())){//私聊信息
                MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneText(message));
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq="+parseObject.getSelfId()+"]")){//被@
                    if(parseObject.getRawMessage().startsWith(
                            "[CQ:at,qq="+parseObject.getSelfId()+"] "+ JSONUtil.getSettingMap().get("identifier"))
                    ){//调用功能
                        if (parseObject.getRawMessage().contains("涩图")) {
                            MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneRandomImage("涩图"));
                        } else if (parseObject.getRawMessage().contains("美图")){
                            MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneRandomImage("美图"));
                        } else if ("owner".equals(parseObject.getSender().get("role"))||"admin".equals(parseObject.getSender().get("role"))) {//管理功能
                            
                        } else {//固定最后
                            MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneText(message));
                        }
                    } else {
                        MsgAction.sendMsg(parseObject, parseObject.getMessageType(), MsgService.msgOneText(message));
                    }

                }
            }
        }
    }

}

