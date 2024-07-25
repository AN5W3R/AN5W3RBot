package org.an5w3r.an5w3rBot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.an5w3r.an5w3rBot.entity.Image;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

public class TextUtil {
    private static final Logger logger = LoggerFactory.getLogger(TextUtil.class);
    //TODO 更换API
    public static String getAiMsg(String sendMsg) {
        try {
            HttpGet httpGet = new HttpGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + sendMsg);
            String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.42";
            httpGet.addHeader("user-agent", user_agent);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            body = body.substring(body.indexOf("content") + 10, body.length() - 2);

            return body;
        } catch (Exception e) {
            return "请求时出了点小问题";
        }
    }
    public static String getGoogleText(String sendMsg){
        try {
            // 设置超时和代理
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(10000)
                    .build();
            HttpHost proxy = new HttpHost("localhost", Integer.parseInt(JSONUtil.getSettingMap().get("proxyPort")));
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
//            String googleReqBody = JSONUtil.getSettingMap().get("google_req_body");
            String googleReqBody = JSONUtil.getGoogleModel();
            String jsonBody = googleReqBody.replace("INSERT_INPUT_HERE", sendMsg);
//            System.out.println(jsonBody);
            request.setEntity(new StringEntity(jsonBody,"UTF-8"));
            request.addHeader("Content-Type", "application/json; charset=UTF-8");
            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(request);

            // 读取响应内容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // 打印响应内容
            System.out.println(result);
            String text = TextUtil.googleAiResponseText(result.toString());
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "出了点小问题";
    }

    public static String googleAiResponseText(String jsonResponse) {
        //将googleResponse的Text提取出来

        JSONObject jsonObject = JSON.parseObject(jsonResponse);
        JSONArray candidates = jsonObject.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject firstPart = parts.getJSONObject(0);
        String text = firstPart.getString("text");

        return text;
    }

    public static String getTranslation(String text, String sourceLang, String targetLang){

        TextResult result = null;
        try {
            String authKey = JSONUtil.getSettingMap().get("deepL_Key");
            Translator translator = new Translator(authKey);
            result = translator.translateText(text, sourceLang, targetLang);
        } catch (DeepLException | InterruptedException | IOException e) {
            return "请求时出了点小问题";
        }

        return result.getText();
    }

    public static String getTextByImage(Image image) throws IOException {
        //如果没有对应文本返回null
        String in = image.getFileName();
        String type = image.getType();

        String imgSrc = JSONUtil.getImageSrcMap().get(type);
        Map<String, String[]> TextJsonMap = JSONUtil.getImageTextMap(imgSrc);
        if (TextJsonMap == null){
            return null;
        }
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

}
