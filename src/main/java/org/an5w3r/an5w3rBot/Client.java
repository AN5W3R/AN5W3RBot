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
import java.util.Map;
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
                    if(parseObject.getRawMessage().contains(JSONUtil.getSettingMap().get("identifier"))){ //调用功能
                        String[] msgStr = parseObject.getRawMessage().split("-");
                        //msgStr[0]@ [1]功能名称 [2...]功能参数
                        //管理功能
                        if(msgStr[0].contains("功能管理")){//控制功能开关
                            if ("owner".equals(parseObject.getSender().get("role"))||"admin".equals(parseObject.getSender().get("role"))){
                                SwitchService.changeFunction(parseObject, msgStr);
                            }
                        }

                        //功能系统
                        if (msgStr[0].contains("翻译") && SwitchService.isFunctionOn(parseObject,"翻译")) {//#翻译 文本 源语言 目标语言
                            MsgAction.sendMsg(parseObject,MsgService.msgTranslationText(msgStr),true);
                        } else {
                            Map<String, String> imageFunctionMap = JSONUtil.getImageFunctionMap();
                            for (String key : imageFunctionMap.keySet()) {
                                if (msgStr[0].contains(key)) {
                                    MsgAction.sendMsg(parseObject, MsgService.msgOneRandomImage(key),true);
                                    break;
                                }
                            }
                        }
                    } else {
                        MsgAction.sendMsg(parseObject, MsgService.msgOneText(message));
                    }

                }
            }
        }
    }

}

