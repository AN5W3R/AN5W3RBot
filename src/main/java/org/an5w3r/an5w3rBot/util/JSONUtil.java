package org.an5w3r.an5w3rBot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JSONUtil {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);
    public static Map<String,String> getSettingMap() throws IOException {
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get("setting.json");

        if (new File(path.toString()).exists()){
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
            byte[] bytes = Files.readAllBytes(path);
            jsonStr = new String(bytes, Charset.forName("UTF-8"));

            Map<String, String> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String>>() {}.getType());

            return resultMap;
        }
        logger.warn("文件路径不正确");
        return null;
    }
    //Text相关
    public static Map<String, String[]> getAtTextMap() throws IOException {//修改为getJson
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(getSettingMap().get("AtMessage"));
        if (new File(path.toString()).exists()){
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
            byte[] bytes = Files.readAllBytes(path);
            jsonStr = new String(bytes, Charset.forName("UTF-8"));

            Map<String, String[]> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String[]>>() {}.getType());

            return resultMap;
        }
        logger.warn("文件路径不正确");
        return null;
    }
    public static Map<String, String[]> getNotAtTextMap() throws IOException {
        String jsonStr = null;

        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(getSettingMap().get("NotAtMessage"));
        if (new File(path.toString()).exists()){
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串

            byte[] bytes = Files.readAllBytes(path);
            jsonStr = new String(bytes, Charset.forName("UTF-8"));
            Map<String, String[]> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String[]>>() {}.getType());

            return resultMap;
        }
        logger.warn("文件路径不正确");
        return null;
    }
    //Image相关
    public static Map<String, String[]> getImageTextMap(String src) throws IOException {//修改为getJson
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(src+"\\text.json");
        if (new File(path.toString()).exists()) {
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
            byte[] bytes = Files.readAllBytes(path);
            jsonStr = new String(bytes, Charset.forName("UTF-8"));

            Map<String, String[]> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String[]>>() {}.getType());

            return resultMap;
        }
        logger.warn("文件路径不正确");
        return null;
    }
    public static Map<String,String> getImageSrcMap() throws IOException {//获取图库路径
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(getSettingMap().get("ImageSrcLib"));
        if (new File(path.toString()).exists()){
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
            byte[] bytes = Files.readAllBytes(path);
            jsonStr = new String(bytes, Charset.forName("UTF-8"));

            Map<String, String> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String>>() {}.getType());

            return resultMap;
        }
        logger.warn("文件路径不正确");
        return null;
    }
    //Google模型相关
    public static String getBaseGoogleModel(){
        String ret = null;
        try {
            // 读取文件内容为字符串
            String content = new String(Files.readAllBytes(Paths.get(getSettingMap().get("googleModel"))), StandardCharsets.UTF_8);

            // 将字符串解析为 JSON 对象
            JSONObject jsonObject = JSON.parseObject(content);

            // 输出 JSON 对象
//            System.out.println(jsonObject.toJSONString());
            ret = jsonObject.toString();
        } catch (IOException e) {
           logger.warn("找不到模型文件");
        }
        return ret;
    }
    public static String addContent(String jsonString,String role,String text){
        JSONObject jsonObject = JSON.parseObject(jsonString);

        // 创建新的消息
        JSONObject newMessage = new JSONObject();
        newMessage.put("role", role);
        JSONArray partsArray = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", text);
        partsArray.add(part);
        newMessage.put("parts", partsArray);

        // 将新消息插入到 JSON 中
        JSONArray contentsArray = jsonObject.getJSONArray("contents");
        int insertPosition = contentsArray.size() - 1; // 倒数第二个位置
        contentsArray.add(insertPosition, newMessage);

        // 输出更新后的 JSON
        return jsonObject.toJSONString();
    }
}
