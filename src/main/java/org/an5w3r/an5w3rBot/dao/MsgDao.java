package org.an5w3r.an5w3rBot.dao;

import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.TextUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

public class MsgDao {
    //对比关键词获取回复,没有关键词则使用ai回复
    public static String getTextByMsg(String in) throws IOException {
        Map<String, String[]> TextJsonMap = JSONUtil.getTextMsgMap();
        for (String s : TextJsonMap.keySet()) {
            if (in.contains(s)) {
                String[] value = TextJsonMap.get(s);
                Random r = new Random();//util.Random
                int index =r.nextInt(value.length);
                return value[index];
            }
        }

        String encodedString = URLEncoder.encode(in, StandardCharsets.UTF_8);
        return TextUtil.getAiMsg(encodedString);
    }

    public static String getTranslation(String[] str){
        if (str.length == 1){
            return "请输入待翻译文本";
        }
        String text = null;
        String sourceLang = null;
        String targetLang = "ZH";
        if (str.length >= 2){//#翻译 文本
            text = str[1];
        }
        if (str.length >= 3){//#翻译 文本 目标语言
            if (str[2].contains("中")) {
                targetLang = "ZH";
            } else if (str[2].contains("英")) {
                targetLang = "EN-US";
            } else if (str[2].contains("日")) {
                targetLang = "JA";
            } else {
                targetLang = str[2];
            }
        }
        if (str.length >= 4){
            if (str[3].contains("中")) {
                sourceLang = "ZH";
            } else if (str[3].contains("英")) {
                sourceLang = "EN-US";
            } else if (str[3].contains("日")) {
                sourceLang = "JA";
            } else {
                sourceLang = str[3];
            }
        }
        return TextUtil.getTranslation(text,sourceLang,targetLang);//#翻译 文本 源语言 目标语言
    }

    public static String getImageByMsg(String in) throws IOException {//暂时没写逻辑,发送随机图
        String src = null;
        if ("涩图".equals(in)){
            src = JSONUtil.getSettingMap().get("SexImageSrc");
        } else if ("美图".equals(in)){
            src = JSONUtil.getSettingMap().get("ImageSrc");
        }
//        System.out.println(src);
        return ImageUtil.getRandomImageLocal(src);
    }
}
