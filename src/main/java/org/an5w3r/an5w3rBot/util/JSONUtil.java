package org.an5w3r.an5w3rBot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JSONUtil {
    public static Map<String,String> getSettingMap() throws IOException {
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get("setting.json");

        // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
        byte[] bytes = Files.readAllBytes(path);
        jsonStr = new String(bytes, Charset.forName("UTF-8"));

        Map<String, String> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String>>() {}.getType());

        return resultMap;
    }
    public static Map<String, String[]> getTextMap() throws IOException {//修改为getJson
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(getSettingMap().get("AtMessageSrc"));

        // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
        byte[] bytes = Files.readAllBytes(path);
        jsonStr = new String(bytes, Charset.forName("UTF-8"));

        Map<String, String[]> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String[]>>() {}.getType());

        return resultMap;
    }
    public static Map<String, String[]> getImageTextMap(String src) throws IOException {//修改为getJson
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(src+"\\text.json");
        //TODO 文件不存在时会报错
        if (new File(path.toString()).exists()) {
            // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
            byte[] bytes = Files.readAllBytes(path);
            jsonStr = new String(bytes, Charset.forName("UTF-8"));

            Map<String, String[]> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String[]>>() {}.getType());

            return resultMap;
        }
        return null;
    }
    public static Map<String,String> getImageSrcMap() throws IOException {//获取图库路径
        String jsonStr = null;
        // 创建一个 Path 对象，表示要读取的文件路径
        Path path = Paths.get(getSettingMap().get("ImageSrcLib"));

        // 使用 Charset 类的 forName 方法，指定字符编码为 UTF-8，并将 byte 数组转换为字符串
        byte[] bytes = Files.readAllBytes(path);
        jsonStr = new String(bytes, Charset.forName("UTF-8"));

        Map<String, String> resultMap = JSON.parseObject(jsonStr, new TypeReference<Map<String, String>>() {}.getType());

        return resultMap;
    }
}
