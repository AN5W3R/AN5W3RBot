package org.example.llonebotbase.dao;

import org.example.llonebotbase.util.MsgUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

public class MsgDao {
    //获取回复的文本
    public static String getTextByMsg(String in) throws IOException {
        Map<String, String[]> TextJsonMap =  MsgUtil.getTextMsgMap();
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

}
