package org.an5w3r.an5w3rBot;

import java.net.*;
import java.io.*;

import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.TextUtil;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.conn.*;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

public class PostExample {
    public static void main(String[] args) {
        try {
            // 设置超时和代理
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(10000)
                    .build();
            HttpHost proxy = new HttpHost("localhost", 7890);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

            // 忽略SSL证书验证（仅用于测试环境）
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            // 创建HttpClient实例
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .setDefaultRequestConfig(requestConfig)
                    .setRoutePlanner(routePlanner)
                    .build();

            // 创建POST请求
            HttpPost request = new HttpPost("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=" + JSONUtil.getSettingMap().get("google_ai_key"));

            // 添加请求体
            String googleReqBody = JSONUtil.getSettingMap().get("google_req_body");
            String jsonBody = googleReqBody.replace("INSERT_INPUT_HERE", "sendMsg");

            request.setEntity(new StringEntity(jsonBody,"UTF-8"));
            request.addHeader("Content-Type", "application/json; charset=UTF-8");
            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(request);

            // 读取响应内容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // 打印响应内容
            System.out.println("回复: " + TextUtil.googleAiResponseText(result.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
