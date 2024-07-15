package org.example.llonebotbase;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

public class LocalChat {
    private static final Logger logger = LoggerFactory.getLogger(LocalChat.class);
    private static final Map<String,Object> jsonMap = getJsonMap();
    public static String ChatByMsg(String in) throws UnsupportedEncodingException {
        for (String s : jsonMap.keySet()) {
            if (in.contains(s)) {
                Object o = jsonMap.get(s);
                if (o.getClass().isArray()) {//有问题
                    String[] stringO = (String[]) o;
                    Random r = new Random();//util.Random
                    int index =r.nextInt(stringO.length);
                    return stringO[index];
                } else {
                    return o.toString();
                }
            }
        }

//        if("你好".equals(in)){
//            return "你也好";
//        }
//        if(in.contains("说话")){
//            return "你好";
//        }
//        if (in.contains("@2044284028")){
//            return "不在";
//        }
//        if(in.contains("小莫")){
//            return "不准压力人,不准压力人,不准压力人";
//        }
        String encodedString = URLEncoder.encode(in, StandardCharsets.UTF_8.toString());
        return "test";
//        return AiOne(encodedString);
    }

    //    该函数用于调用qingyunke生成聊天的回复
    public static String AiOne(String sendMsg) {
        try {
            HttpGet httpGet = new HttpGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + sendMsg);
            String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.42";
            httpGet.addHeader("user-agent", user_agent);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            body = body.substring(body.indexOf("content") + 10, body.length() - 2);
            logger.info("AiOne={}", body);
            return body;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static Map<String,Object> getJsonMap(){
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get("src/main/resources/Chat.json");
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
        return jsonObject;
//        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }
    }
}
