package org.example.llonebotbase;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LocalChat {
    private static final Logger logger = LoggerFactory.getLogger(LocalChat.class);

    public static String ChatByMsg(String in) throws UnsupportedEncodingException {
        if("你好".equals(in)){
            return "private test success";
        }
        if(in.contains("说话")){
            return "你好";
        }
        if (in.contains("@2044284028")){
            return "不在";
        }
        if(in.contains("小莫")){
            return "不准压力人,不准压力人,不准压力人";
        }
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
}
