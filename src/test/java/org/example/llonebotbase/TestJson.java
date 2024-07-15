package org.example.llonebotbase;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


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
}
