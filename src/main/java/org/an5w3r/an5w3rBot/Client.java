package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
    public void onMessage(String messageStr) throws IOException, ExecutionException, InterruptedException {
//        System.out.println("\n"+messageStr);
        if (messageStr.contains("\"post_type\":\"message\"")) {//处理message信息
            Message message = JSONObject.parseObject(messageStr, Message.class);//JSON转换为对象

            if("private".equals(message.getMessageType())){//私聊信息
                MsgAction.sendMsg(message
                        ,new MsgItem(TextDao.getAtTextByMsg(message.getRawMessage())));
            }
            if ("group".equals(message.getMessageType())) {//群聊信息
//                System.out.println(message.getSender());
//                if (message.getSender().getUserId().equals("1542338612")){//小莫说话
//                    MsgAction.deleteMsg(message);
//                    GroupAction.setGroupCard(message,"笨蛋");
//                    GroupAction.setGroupBan(message,1);
//                }


                if(message.getRawMessage().contains("[CQ:at,qq="+message.getSelfId()+"]")){//被@

//                    message.getRawMessage().replace("[CQ:at,qq="+message.getSelfId()+"]","");
                    if(message.getRawMessage().contains(JSONUtil.getSettingMap().get("identifier"))){ //调用功能
                        boolean flag = true;
//                        String[] msgStr = message.getRawMessage().split("-");
                        String[] splitMsg = message.splitMsg();
                        //功能管理
                        if(splitMsg[0].contains("功能管理")){
                            flag=false;
                            if ("owner".equals(message.getSender().getRole())||"admin".equals(message.getSender().getRole())){
                                SwitchService.changeFunction(message);
                            } else {
                                MsgAction.sendMsg(message
                                        ,MsgItem.atItem(message.getUserId())
                                        ,new MsgItem("你没有管理员权限"));
                            }
                        }

                        //TODO 把功能包装成类,input:拆分后的msgStr,msgStr[0]为功能名称
                        //内置功能系统
                        if(flag){
                            for (String key : SwitchService.functionList) {//查找功能列表中是否有此功能
                                if (splitMsg[0].contains(key)) {
                                    flag=false;
                                    if (SwitchService.isFunctionOn(message,key)) {
                                        switch (key){//功能列表
                                            case "翻译":{
                                                MsgAction.sendMsg(message
                                                        ,MsgItem.atItem(message.getUserId())
                                                        ,new MsgItem(TextDao.getTranslation(message)));
                                                break;
                                            }
                                            case "创建":{
                                                GameTeamService.addTeam(message);
                                                break;
                                            }
                                            case "加入":{
                                                GameTeamService.joinTeam(message);
                                                break;
                                            }
                                            case "退出":{
                                                GameTeamService.leaveTeam(message);
                                                break;
                                            }
                                            case "解散":{
                                                GameTeamService.removeTeam(message);
                                                break;
                                            }
                                            case "开了":{
                                                GameTeamService.playTeam(message);
                                                break;
                                            }
                                        }
                                    } else {
                                        MsgAction.sendMsg(message
                                                ,MsgItem.atItem(message.getUserId())
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
                                if (splitMsg[0].contains(key)) {
                                    if (SwitchService.isFunctionOn(message,key)) {
                                        Image image = ImageDao.getImageByMsg(key);
                                        MsgAction.sendMsg(message
                                                ,MsgItem.atItem(message.getUserId())
                                                ,new MsgItem("image","file", image.getFile())
                                                ,new MsgItem(image.getText())
                                        );//参数即是功能名
                                        break;
                                    } else {
                                        MsgAction.sendMsg(message
                                                ,MsgItem.atItem(message.getSender().getUserId())
                                                ,new MsgItem(key+"功能已被关闭"));
                                        break;
                                    }

                                }
                            }
                        }
                    } else {
                        MsgAction.sendMsg(message
                                ,MsgItem.atItem(message.getSender().getUserId())
                                ,new MsgItem(TextDao.getAtTextByMsg(message.getRawMessage())));
                    }

                }else {//无@对话
                    Map<String, String[]> map = JSONUtil.getNotAtTextMap();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {//先判断在notAtTextMap中是否有key
                        if (message.getRawMessage().contains(key)) {
                            MsgAction.sendMsg(message,new MsgItem(TextDao.getNotAtTextByMsg(key)));
                        }
                    }
                }

            }
        }
    }

}

