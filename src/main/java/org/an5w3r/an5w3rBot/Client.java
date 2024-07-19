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
                MsgAction.sendMsg(parseObject, MsgService.oneText(message));
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq="+parseObject.getSelfId()+"]")){//被@

                    if(parseObject.getRawMessage().contains(JSONUtil.getSettingMap().get("identifier"))){ //调用功能
                        boolean flag = true;
                        String[] msgStr = parseObject.getRawMessage().split("-");

                        //功能管理
                        if(msgStr[0].contains("功能管理")){
                            flag=false;
                            if ("owner".equals(parseObject.getSender().get("role"))||"admin".equals(parseObject.getSender().get("role"))){
                                SwitchService.changeFunction(parseObject, msgStr);
                            } else {
                                MsgAction.sendMsg(parseObject
                                        ,MsgService.atQQ(parseObject.getSender().get("user_id"))
                                        ,MsgService.tipsText("你没有管理员权限"));
                            }
                        }
                        //内置功能系统
                        if(flag){
                            for (String key : SwitchService.functionList) {//查找功能列表中是否有此功能
                                if (msgStr[0].contains(key)) {
                                    flag=false;
                                    if (SwitchService.isFunctionOn(parseObject,key)) {
                                        switch (key){//功能列表
                                            case "翻译":{
                                                MsgAction.sendMsg(parseObject
                                                        ,MsgService.atQQ(parseObject.getSender().get("user_id"))
                                                        ,MsgService.translationText(msgStr));
                                                break;
                                            }
                                        }
                                    } else {
                                        MsgAction.sendMsg(parseObject
                                                ,MsgService.atQQ(parseObject.getSender().get("user_id"))
                                                ,MsgService.tipsText(key+"功能已被关闭"));
                                    }

                                }
                            }
                        }
                        //图库功能系统
                        if (flag){
                            Map<String, String> imageFunctionMap = JSONUtil.getImageFunctionMap();
                            for (String key : imageFunctionMap.keySet()) {//查找图库功能列表
                                if (msgStr[0].contains(key)) {
                                    if (SwitchService.isFunctionOn(parseObject,key)) {
                                        MsgAction.sendMsg(parseObject
                                                ,MsgService.atQQ(parseObject.getSender().get("user_id"))
                                                ,MsgService.randomImage(key));//参数即是功能名
                                        break;
                                    } else {
                                        MsgAction.sendMsg(parseObject
                                                ,MsgService.atQQ(parseObject.getSender().get("user_id"))
                                                ,MsgService.tipsText(key+"功能已被关闭"));
                                        break;
                                    }

                                }
                            }
                        }
                    } else {
                        MsgAction.sendMsg(parseObject
                                ,MsgService.atQQ(parseObject.getSender().get("user_id"))
                                , MsgService.oneText(message));
                    }

                }
            }
        }
    }

}

