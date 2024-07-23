package org.an5w3r.an5w3rBot.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MsgItem {
    String type;
    Map<String,String> data;

    public MsgItem() {
    }

    public MsgItem(String text){
        this("text","text","\n"+ text);
    }

    public MsgItem(String type, String dataType, String message) {
        this.setType(type);

        Map<String, String> data  = new HashMap<>();
        data.put(dataType,message);

        this.setData(data);
    }

    public static MsgItem atItem(String QQ){
        return new MsgItem("at", "qq", QQ);
    }
}
