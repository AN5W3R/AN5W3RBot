package org.an5w3r.an5w3rBot.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class Sender {
    @JSONField(name = "user_id")
    private String userId;
    private String nickname;
    private String card;
    private String sex;
    private String age;
    private String area;
    private String level;
    private String role;
    private String title;
}
