package org.an5w3r.an5w3rBot.util;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.an5w3r.an5w3rBot.entity.Image;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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