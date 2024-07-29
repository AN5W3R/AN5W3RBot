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


        processImageFunction(message, splitMsg[0]);
    }

    private static void handleFunctionManagement(Message message) throws IOException {
        if ("owner".equals(message.getSender().getRole()) || "admin".equals(message.getSender().getRole())) {
            SwitchService.changeFunction(message);
        } else {
            MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId()), new MsgItem("你没有管理员权限"));
        }
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
                startGuessCharacter(message);
                break;
            case "答案":
                submitGuessCharacterAnswer(message);
                break;
        }
    }

    private static Map<String,Image> guessImage = new HashMap<>();
    private static int countGuess = 0;
    private static void startGuessCharacter(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();
        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        String galleryName = splitMsg[1];
        Image image = ImageDao.getImageByMsg(galleryName);
        if (image != null) {
            guessImage.put(message.getGroupId(),image);
            String cropImage = ImageUtil.cropImage(image.getFile());


            MsgAction.sendMsg(message
                    , MsgItem.atItem(message.getUserId())
                    , new MsgItem("image", "file", cropImage)
                    , new MsgItem("请猜这个角色是谁！"));
        } else {
            MsgAction.sendMsg(message, new MsgItem("图库名称不正确或不存在"));
        }
    }

    private static void submitGuessCharacterAnswer(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();
        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        String answer = splitMsg[1];
        if (guessImage.get(message.getGroupId()) != null) {
            String[] correctAnswers = guessImage.get(message.getGroupId()).getTexts();
            for (String correctAnswer : correctAnswers) {
                if (answer.contains(correctAnswer)) {
                    MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                            , new MsgItem("恭喜你，答案正确！")
                            , new MsgItem("image","file", guessImage.get(message.getGroupId()).getFile())
                    );
                    guessImage.put(message.getGroupId(),null);
                    countGuess = 0;
                    return;
                }
            }
            MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                    , new MsgItem("回答错误！")
            );
            countGuess++;
            int  chance = Integer.parseInt(JSONUtil.getSettingMap().get("guessChance"));
            if (countGuess>=chance){
                MsgAction.sendMsg(message
                        , MsgItem.atItem(message.getUserId())
                        , new MsgItem("\n正确答案为"+ Arrays.toString(guessImage.get(message.getGroupId()).getTexts()))
                        , new MsgItem("image","file", guessImage.get(message.getMessageId()).getFile())
                );
                guessImage.put(message.getGroupId(),null);
                countGuess = 0;
            }

//            guessImage = null; // 清除当前猜测的图片
        } else {
            MsgAction.sendMsg(message, new MsgItem("没有找到对应的答案,请确定是否存在对应文本"));
        }
    }




    private static void processImageFunction(Message message, String msg) throws IOException {//图库功能
        Map<String, String> imageFunctionMap = JSONUtil.getImageSrcMap();
        for (String key : imageFunctionMap.keySet()) {
            if (msg.contains(key)) {
                if (SwitchService.isFunctionOn(message, key)) {
                    Image image = ImageDao.getImageByMsg(key);
                    MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId()), new MsgItem("image", "file", image.getFile()), new MsgItem(image.getText()));
                    break;
                } else {
                    MsgAction.sendMsg(message, MsgItem.atItem(message.getSender().getUserId()), new MsgItem(key + "功能已被关闭"));
                    break;
                }
            }
        }
    }

}
