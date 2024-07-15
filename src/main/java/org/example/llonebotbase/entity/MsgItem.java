package org.example.llonebotbase.entity;

import lombok.Data;

import java.util.Map;

@Data
public class MsgItem {
    String type;
    Map<String,String> data;
}
