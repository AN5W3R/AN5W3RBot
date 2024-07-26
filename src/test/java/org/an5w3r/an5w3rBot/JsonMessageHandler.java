package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonMessageHandler {
    public static void main(String[] args) {
        // 现有的 JSON 数据
        String jsonString = "{\"safetySettings\":[{\"threshold\":\"BLOCK_NONE\",\"category\":\"HARM_CATEGORY_HARASSMENT\"},{\"threshold\":\"BLOCK_NONE\",\"category\":\"HARM_CATEGORY_HATE_SPEECH\"},{\"threshold\":\"BLOCK_NONE\",\"category\":\"HARM_CATEGORY_SEXUALLY_EXPLICIT\"},{\"threshold\":\"BLOCK_NONE\",\"category\":\"HARM_CATEGORY_DANGEROUS_CONTENT\"}],\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":\"假设你是一只猫娘\"}]},{\"role\":\"model\",\"parts\":[{\"text\":\"喵呜？( 歪头 ) 你好呀！你想做什么呢？是要\"}]},{\"role\":\"user\",\"parts\":[{\"text\":\"你好\"}]},{\"role\":\"model\",\"parts\":[{\"text\":\"喵呜！( 开心地摇尾巴 ) 你好呀！很高兴见到你！\"}]},{\"role\":\"user\",\"parts\":[{\"text\":\"爱你\"}]},{\"role\":\"user\",\"parts\":[{\"text\":\"(摸胸)\"}]}],\"generationConfig\":{\"topK\":64,\"temperature\":1,\"responseMimeType\":\"text/plain\",\"topP\":0.95,\"maxOutputTokens\":8192}}";
        JSONObject jsonObject = JSON.parseObject(jsonString);

        // 创建新的消息
        JSONObject newMessage = new JSONObject();
        newMessage.put("role", "model");
        JSONArray partsArray = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", "只能摸一下哦");
        partsArray.add(part);
        newMessage.put("parts", partsArray);

        // 将新消息插入到 JSON 中
        JSONArray contentsArray = jsonObject.getJSONArray("contents");
        contentsArray.add(newMessage);

        // 输出更新后的 JSON
        System.out.println(jsonObject.toJSONString());
    }
}
