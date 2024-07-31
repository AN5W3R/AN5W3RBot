package org.an5w3r.an5w3rBot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.an5w3r.an5w3rBot.entity.Content;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TextUtil {
    private static final Logger logger = LoggerFactory.getLogger(TextUtil.class);
    private static Map<String, List<Content>> contents = new HashMap<>();

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
    //谷歌ai
    public static String getGoogleText(Message message){
        String sendMsg = message.atMsg();
        String id = message.getGroupId();
        if (message.getGroupId()==null) {
            id=message.getUserId();
        }
        List<Content> contentList = contents.get(id);
        if (contentList==null) {
            contentList = new LinkedList<>();
            contents.put(id,contentList);
        }

        try {
            logger.info("正在获取Google聊天信息");
            // 设置超时和代理
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(10000)
                    .setSocketTimeout(15000)
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
            String googleReqBody = JSONUtil.getBaseGoogleModel();

            for (Content content : contents.get(id)) {
                googleReqBody = JSONUtil.addContent(googleReqBody,content.getRole(),content.getText());
            }
            contents.get(id).add(new Content("user",sendMsg));

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
//            System.out.println(result);
            String text = TextUtil.googleAiResponseText(result.toString());
            //清理换行符
            text = text.replaceAll("\\\\n", "").replaceAll("\\n", "");
            contents.get(id).add(new Content("model",text));
            return text;
        } catch (Exception e) {
            return "请求超时了";
        }
    }
    public static String googleAiResponseText(String jsonResponse) {
        // 将googleResponse的Text提取出来
        JSONObject jsonObject = JSON.parseObject(jsonResponse);
        JSONArray candidates = jsonObject.getJSONArray("candidates");
        if (candidates != null && !candidates.isEmpty()) {
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            if (content != null) {
                JSONArray parts = content.getJSONArray("parts");
                if (parts != null && !parts.isEmpty()) {
                    JSONObject firstPart = parts.getJSONObject(0);
                    String text = firstPart.getString("text");
                    if (text != null && !text.isEmpty()) {
                        return text;
                    }
                }
            }
        }

        // 如果text字段为空或不存在，尝试获取message字段
        String message = jsonObject.getString("message");
        return "";
    }
    //翻译
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

    //获取图片文本
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
            if (in.equals(s)) {
                String[] value = TextJsonMap.get(s);
                Random r = new Random();//util.Random
                int index =r.nextInt(value.length);
                return value[index];
            }
        }
        return null;
    }
    public static String[] getTextsByImage(Image image) throws IOException {
        //如果没有对应文本返回null
        String in = image.getFileName();
        String type = image.getType();

        String imgSrc = JSONUtil.getImageSrcMap().get(type);
        Map<String, String[]> TextJsonMap = JSONUtil.getImageTextMap(imgSrc);
        if (TextJsonMap == null){
            return null;
        }
        for (String s : TextJsonMap.keySet()) {
            if (in.equals(s)) {
                String[] value = TextJsonMap.get(s);
                return value;
            }
        }
        return null;
    }
    public static String[] getFortuneText() throws IOException {
        String filePath = JSONUtil.getSettingMap().get("fortuneSrc") + "\\text.json";
        String textList = new String(Files.readAllBytes(Paths.get(filePath)));

        // 解析 JSON 字符串
        JSONArray jsonArray = JSON.parseArray(textList);

        // 创建随机数生成器
        Random random = new Random();

        // 从 JSON 数组中随机选择一条记录
        int randomIndex = random.nextInt(jsonArray.size());
        JSONObject randomObject = jsonArray.getJSONObject(randomIndex);

        // 获取 title 和 content 值
        String title = randomObject.getString("title");
        String content = randomObject.getString("content");

        // 返回 title 和 content 作为字符串数组
        return new String[]{title, content};
    }
}
