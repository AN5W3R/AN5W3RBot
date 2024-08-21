package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.dao.ImageDao;
import org.an5w3r.an5w3rBot.entity.Font;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.TextUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ImageService {
    private static Map<String, Image> guessImage = new HashMap<>();
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
    public static boolean checkGuessCharacterAnswer(Message message) throws IOException {
        Image image = guessImage.get(message.getGroupId());
        if (image==null) {
            return false;
        }
        String[] correctAnswers = image.getTexts();
        String file = image.getFile();
        if (correctAnswers==null){
            return false;
        }
        String answer = message.atMsg();
        for (String correctAnswer : correctAnswers) {
            if (answer.contains(correctAnswer)) {
                MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                        , new MsgItem("恭喜你，答案正确！")
                        , new MsgItem("image","file", file)
                );
                guessImage.put(message.getGroupId(),null);
                return true;
            }
        }
        return false;
    }
    public static void submitGuessCharacterAnswer(Message message) throws IOException {
        if (guessImage.get(message.getGroupId()) != null) {
            String[] splitMsg = message.splitMsg();

            if (splitMsg.length < 2) {
                String[] correctAnswers = guessImage.get(message.getGroupId()).getTexts();
                String file = guessImage.get(message.getGroupId()).getFile();
                MsgAction.sendMsg(message
                        , MsgItem.atItem(message.getUserId())
                        , new MsgItem("\n正确答案为"+ Arrays.toString(correctAnswers))
                        , new MsgItem("image","file",file)
                );
                guessImage.put(message.getGroupId(),null);
                return;
            }
            String answer = splitMsg[1];
            String[] correctAnswers = guessImage.get(message.getGroupId()).getTexts();
            String file = guessImage.get(message.getGroupId()).getFile();
            if (correctAnswers==null){
                MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                        , new MsgItem("没有文本！")
                );
                return;
            }
            for (String correctAnswer : correctAnswers) {
                if (answer.contains(correctAnswer)) {
                    MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                            , new MsgItem("恭喜你，答案正确！")
                            , new MsgItem("image","file", file)
                    );
                    guessImage.put(message.getGroupId(),null);
                    return;
                }
            }
            MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId())
                    , new MsgItem("回答错误！")
            );
        } else {
            MsgAction.sendMsg(message, new MsgItem("没有文本"));
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

    public static void getFortune(Message message) throws IOException {
        //获取底图
        Image fortuneImage = ImageDao.getFortuneImage();
        //获取text
        String[] fortuneText = TextUtil.getFortuneText();
        //获取font
        Font font = JSONUtil.getFont();
        //插入文字
        String pic = ImageUtil.fortuneInsertText(fortuneImage, fortuneText, font);

        MsgAction.sendMsg(message, MsgItem.atItem(message.getUserId()), new MsgItem("image", "file", pic));
    }

}
