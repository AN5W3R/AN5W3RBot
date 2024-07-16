package org.an5w3r.an5w3rBot.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {
    public static String getRandomImageUrl(){
//        https://api.sevin.cn/api/ecy.php
        StringBuilder content = new StringBuilder();
        try {
            // 创建URL对象
            URL url = new URL("https://api.sevin.cn/api/ecy.php");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 获取响应码
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 读取响应内容
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // 关闭流
            in.close();

            // 打印响应内容
            System.out.println("Response Content: " + content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
