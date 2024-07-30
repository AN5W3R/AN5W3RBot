package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.dao.ImageDao;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ImageService {
    private static Map<String, Image> guessImage = new HashMap<>();
    private static int countGuess = 0;
    public static void startGuessCharacter(Message message) throws IOException {
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

    public static void submitGuessCharacterAnswer(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();
        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        String answer = splitMsg[1];
        if (guessImage.get(message.getGroupId()) != null) {
            String[] correctAnswers = guessImage.get(message.getGroupId()).getTexts();
            String file = guessImage.get(message.getGroupId()).getFile();
            if (correctAnswers==null){
                MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                        , new MsgItem("没有对应文本！")
                );
                return;
            }
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
            String guessChance = JSONUtil.getSettingMap().get("guessChance");
            int  chance = Integer.parseInt(guessChance);
            if (countGuess>=chance){
                MsgAction.sendMsg(message
                        , MsgItem.atItem(message.getUserId())
                        , new MsgItem("\n正确答案为"+ Arrays.toString(correctAnswers))
                        , new MsgItem("image","file",file)
                );
                guessImage.put(message.getGroupId(),null);
                countGuess = 0;
            }

        } else {
            MsgAction.sendMsg(message, new MsgItem("没有找到对应的答案,请确定是否存在对应文本"));
        }
    }

    public static void processImageFunction(Message message, String msg) throws IOException {//图库功能
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
