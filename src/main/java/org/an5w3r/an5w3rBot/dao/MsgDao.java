package org.an5w3r.an5w3rBot.dao;

import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.MsgUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

public class MsgDao {
    //获取回复的文本
    public static String getTextByMsg(String in) throws IOException {
//        Map<String, String[]> TextJsonMap =  MsgUtil.getTextMsgMap();
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
        return MsgUtil.getAiMsg(encodedString);
    }

    public static String getImageByMsg(String in) throws IOException {
//        Map<String, String[]> TextJsonMap =  MsgUtil.getTextMsgMap();
//        for (String s : TextJsonMap.keySet()) {
//            if (in.contains(s)) {
//                String[] value = TextJsonMap.get(s);
//                Random r = new Random();//util.Random
//                int index =r.nextInt(value.length);
//                return value[index];
//            }
//        }
        return ImageUtil.getRandomImageLocal();
    }
}
