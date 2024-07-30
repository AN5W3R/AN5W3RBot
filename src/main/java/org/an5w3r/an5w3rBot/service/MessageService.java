package org.an5w3r.an5w3rBot.service;

import com.alibaba.fastjson.JSONObject;
import org.an5w3r.an5w3rBot.action.GroupAction;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.dao.ImageDao;
import org.an5w3r.an5w3rBot.dao.TextDao;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class MessageService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MessageService.class);
    public static void handleMessage(String messageStr) throws IOException, ExecutionException, InterruptedException {
        Message message = JSONObject.parseObject(messageStr, Message.class);

        if ("private".equals(message.getMessageType())) {
            handlePrivateMessage(message);
        } else if ("group".equals(message.getMessageType())) {
            handleGroupMessage(message);
        }
    }

    private static void handlePrivateMessage(Message message) throws IOException {
        logger.info("\n收到好友[" + message.getSender().getNickname() + "]的消息：" + message.getRawMessage() + "\n");
        MsgAction.sendMsg(message, new MsgItem(TextDao.getAtTextByMsg(message)));
    }
    private static void handleGroupMessage(Message message) throws IOException {
        logger.info("\n收到群" + message.getGroupId() + "-[" + message.getSender().getCard() + "]的消息：" + message.getRawMessage() + "\n");

        if (message.getRawMessage().contains("[CQ:at,qq=" + message.getSelfId() + "]")) {
            handleAtMessage(message);
        } else {
            handleNonAtMessage(message);
        }
    }

    private static void handleAtMessage(Message message) throws IOException {//被@
        if (message.getRawMessage().contains(JSONUtil.getSettingMap().get("identifier"))) {
            processFunction(message);
        } else {
            MsgAction.sendMsg(message, MsgItem.atItem(message.getSender().getUserId()), new MsgItem(TextDao.getAtTextByMsg(message)));
        }
    }
    private static void handleNonAtMessage(Message message) throws IOException {//关键句式触发小黑屋功能
        Map<String, String[]> map = JSONUtil.getNotAtTextMap();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String noAtMsg = message.atMsg();
            if (Pattern.matches(key, noAtMsg)) {
                MsgAction.sendMsg(message, new MsgItem(TextDao.getNotAtTextByMsg(key)));
                MsgAction.deleteMsg(message);
                GroupAction.setGroupBan(message, 1);
                break;
            }
        }
    }
    private static void processFunction(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg[0].contains("功能管理")) {
            handleFunctionManagement(message);
            return;
        }

        for (String key : SwitchService.functionList) {
            if (splitMsg[0].contains(key)) {
                if (SwitchService.isFunctionOn(message, key)) {
                    executeFunction(message, key);
                } else {
                    MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId()), new MsgItem(key + "功能已被关闭"));
                }
                return;
            }
        }


        ImageService.processImageFunction(message, splitMsg[0]);
    }

    private static void executeFunction(Message message, String key) throws IOException {
        switch (key) {
            case "翻译":
                handleTranslation(message);
                break;
            case "创建":
            case "加入":
            case "退出":
            case "解散":
            case "开了":
                handleTeamFunction(message, key);
                break;
            case "猜角色":
            case "答案":
                handleGuessCharacterFunction(message, key);
                break;

        }

    }

    private static void handleTranslation(Message message) throws IOException {
        MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId()), new MsgItem(TextDao.getTranslation(message)));
    }
    private static void handleTeamFunction(Message message, String key) throws IOException {
        switch (key) {
            case "创建":
                GameTeamService.addTeam(message);
                break;
            case "加入":
                GameTeamService.joinTeam(message);
                break;
            case "退出":
                GameTeamService.leaveTeam(message);
                break;
            case "解散":
                GameTeamService.removeTeam(message);
                break;
            case "开了":
                GameTeamService.playTeam(message);
                break;
        }
    }
    private static void handleGuessCharacterFunction(Message message, String key) throws IOException {
        switch (key) {
            case "猜角色":
                ImageService.startGuessCharacter(message);
                break;
            case "答案":
                ImageService.submitGuessCharacterAnswer(message);
                break;
        }
    }


    private static void handleFunctionManagement(Message message) throws IOException {
        if ("owner".equals(message.getSender().getRole()) || "admin".equals(message.getSender().getRole())) {
            SwitchService.changeFunction(message);
        } else {
            MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId()), new MsgItem("你没有管理员权限"));
        }
    }

}
