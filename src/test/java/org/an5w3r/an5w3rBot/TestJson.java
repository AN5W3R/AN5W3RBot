package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.Request;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TestJson {

    @Test
    public void testJson(){
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get("src/main/resources/AtMessage.json");
        try {
            // 使用 Files 类的 readAllBytes 方法，将文件的所有字节读取到一个 byte 数组中
            byte[] bytes = Files.readAllBytes(path);
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
            jsonStr = new String(bytes, Charset.forName("UTF-8"));

        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    @Test
    public void testJson2(){
        Request<Object> paramsRequest = new Request<>();//用于发送请求的对象
        paramsRequest.setAction("send_group_msg");//设置发送群消息
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", 123456);//设置要发送的群

        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem();
        item.setType("text");
        Map<String, String> data  = new HashMap<>();
        data.put("text", "333");

        item.setData(data);
        msgList.add(item);
        params.put("message",msgList);


        paramsRequest.setParams(params);

        String msg = JSONObject.toJSONString(paramsRequest);

        System.out.println(msg);

    }
    @Test
    public void testImage(){
        System.out.println(ImageUtil.getRandomImageUrl());
    }

    @Test
    public void testJSONSetting() throws IOException {
        System.out.println(JSONUtil.getSettingMap());
    }
    @Test
    public void testRandom(){
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            System.out.print(random.nextInt(10)+" ");
        }
    }

    @Test
    public void testGetFriendList(){
    }
}
