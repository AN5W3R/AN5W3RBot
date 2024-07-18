package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.service.MsgService;
import org.an5w3r.an5w3rBot.service.SwitchService;
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
//        System.out.println("\n"+message);
        if (message.contains("\"post_type\":\"message\"")) {//处理message信息
            Message parseObject = JSONObject.parseObject(message, Message.class);//JSON转换为对象

            if("private".equals(parseObject.getMessageType())){//私聊信息
                MsgAction.sendMsg(parseObject, MsgService.msgOneText(message));
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq="+parseObject.getSelfId()+"]")){//被@
                    if(parseObject.getRawMessage().startsWith(
                            "[CQ:at,qq="+parseObject.getSelfId()+"] "+ JSONUtil.getSettingMap().get("identifier"))
                    ){ //调用功能
                        String[] msgStr = parseObject.getRawMessage().split(" ");
                        //管理系统
                        if ("owner".equals(parseObject.getSender().get("role"))||"admin".equals(parseObject.getSender().get("role"))) {//管理功能
                            if(msgStr[1].contains("功能管理")){//控制功能开关
                                SwitchService.changeFunction(parseObject, msgStr);
                            }
                        }

                        //@功能系统
                        if (msgStr[1].contains("涩图") && SwitchService.isFunctionOn(parseObject,"涩图")) {
                            MsgAction.sendMsg(parseObject, MsgService.msgOneRandomImage("涩图"));
                        } else if (msgStr[1].contains("美图") && SwitchService.isFunctionOn(parseObject,"美图")){
                            MsgAction.sendMsg(parseObject, MsgService.msgOneRandomImage("美图"));
                        }

                    } else {
                        MsgAction.sendMsg(parseObject, MsgService.msgOneText(message));
                    }

                }
            }
        }
    }

}

