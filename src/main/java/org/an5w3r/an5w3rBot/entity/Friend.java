package org.an5w3r.an5w3rBot.entity;

import lombok.Data;
 
@Data
public class Friend {
 
    private String user_id;
    private String comment;//好友请求验证消息
    private String flag;
}