package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.an5w3r.an5w3rBot.action.GroupAction;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.dao.ImageDao;
import org.an5w3r.an5w3rBot.dao.TextDao;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.service.GameTeamService;
import org.an5w3r.an5w3rBot.service.SwitchService;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
                MsgAction.sendMsg(parseObject
                        ,new MsgItem(TextDao.getTextByMsg(message)));
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
//                if (parseObject.getSender().get("user_id").equals("1542338612")){//小莫说话
//                    MsgAction.deleteMsg(parseObject);
//                    GroupAction.setGroupCard(parseObject,"笨蛋");
//                    GroupAction.setGroupBan(parseObject,1);
//                }

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
                                        ,MsgItem.atItem(parseObject.getUserId())
                                        ,new MsgItem("你没有管理员权限"));
                            }
                        }
                        //TODO 把功能包装成类,input:拆分后的msgStr,msgStr[0]为功能名称
                        //内置功能系统
                        if(flag){
                            for (String key : SwitchService.functionList) {//查找功能列表中是否有此功能
                                if (msgStr[0].contains(key)) {
                                    flag=false;
                                    if (SwitchService.isFunctionOn(parseObject,key)) {
                                        switch (key){//功能列表
                                            case "翻译":{
                                                MsgAction.sendMsg(parseObject
                                                        ,MsgItem.atItem(parseObject.getUserId())
                                                        ,new MsgItem(TextDao.getTranslation(msgStr)));
                                                break;
                                            }
                                            case "摇人":{
                                                GameTeamService.addTeam(parseObject,msgStr);
                                                break;
                                            }
                                            case "加入":{
                                                GameTeamService.joinTeam(parseObject,msgStr);
                                                break;
                                            }
                                            case "退出":{
                                                GameTeamService.leaveTeam(parseObject,msgStr);
                                                break;
                                            }
                                            case "解散":{
                                                GameTeamService.removeTeam(parseObject,msgStr);
                                                break;
                                            }
                                            case "开了":{
                                                GameTeamService.playTeam(parseObject,msgStr);
                                                break;
                                            }
                                        }
                                    } else {
                                        MsgAction.sendMsg(parseObject
                                                ,MsgItem.atItem(parseObject.getUserId())
                                                ,new MsgItem(key+"功能已被关闭"));
                                    }

                                }
                            }
                        }
                        //图库功能系统
                        if (flag){
                            //TODO 更新图库功能
                            Map<String, String> imageFunctionMap = JSONUtil.getImageSrcMap();
                            for (String key : imageFunctionMap.keySet()) {//查找图库功能列表
                                if (msgStr[0].contains(key)) {
                                    if (SwitchService.isFunctionOn(parseObject,key)) {
                                        Image image = ImageDao.getImageByMsg(key);
                                        MsgAction.sendMsg(parseObject
                                                ,MsgItem.atItem(parseObject.getUserId())
                                                ,new MsgItem("image","file", image.getFile())
                                                ,new MsgItem(image.getText())
                                        );//参数即是功能名
                                        break;
                                    } else {
                                        MsgAction.sendMsg(parseObject
                                                ,MsgItem.atItem(parseObject.getSender().get("user_id"))
                                                ,new MsgItem(key+"功能已被关闭"));
                                        break;
                                    }

                                }
                            }
                        }
                    } else {
                        MsgAction.sendMsg(parseObject
                                ,MsgItem.atItem(parseObject.getSender().get("user_id"))
                                ,new MsgItem(TextDao.getTextByMsg(message)));
                    }

                }
            }
        }
    }

}

