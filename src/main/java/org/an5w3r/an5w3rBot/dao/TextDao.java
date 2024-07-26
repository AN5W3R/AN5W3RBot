package org.an5w3r.an5w3rBot.dao;

import org.an5w3r.an5w3rBot.Client;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

public class TextDao {
    private static final Logger logger = LoggerFactory.getLogger(TextDao.class);

    //接收一个参数,来源聊天
    public static String getAtTextByMsg(Message message) throws IOException {
        String in = message.atMsg();
        Map<String, String[]> TextJsonMap = JSONUtil.getAtTextMap();
        for (String s : TextJsonMap.keySet()) {
            if (in.contains(s)) {
                String[] value = TextJsonMap.get(s);
                Random r = new Random();//util.Random
                int index =r.nextInt(value.length);
                return value[index];
            }
        }

        return TextUtil.getGoogleText(message);
    }
    public static String getNotAtTextByMsg(String in) throws IOException {//无@关键词
        Map<String, String[]> TextJsonMap = JSONUtil.getNotAtTextMap();
        assert TextJsonMap != null;
        for (String s : TextJsonMap.keySet()) {
            if (in.contains(s)) {
                String[] value = TextJsonMap.get(s);
                Random r = new Random();//util.Random
                int index =r.nextInt(value.length);
                return value[index];
            }
        }

        return null;
    }
    //翻译功能
    public static String getTranslation(Message message){
        String[] str = message.splitMsg();
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

}
