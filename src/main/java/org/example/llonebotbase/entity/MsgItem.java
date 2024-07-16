package org.example.llonebotbase.entity;

import lombok.Data;
import org.example.llonebotbase.dao.MsgDao;

import java.util.HashMap;
import java.util.Map;

@Data
public class MsgItem {
    String type;
    Map<String,String> data;

    public MsgItem() {
    }

    public MsgItem(String type, String dataType, String message) {
        this.setType(type);

        Map<String, String> data  = new HashMap<>();
        data.put(dataType,message);

        this.setData(data);
    }
}
