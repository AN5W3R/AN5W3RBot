package org.an5w3r.an5w3rBot.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Message implements Serializable {
 
    @JSONField(name = "post_type")
    private String postType;
    @JSONField(name = "meta_event_type")
    private String metaEventType;
    @JSONField(name = "message_type")
    private String messageType;
    @JSONField(name = "notice_type")
    private String noticeType;
    // 操作人id 比如群管理员a踢了一个人,那么该值为a的qq号
    @JSONField(name = "operator_id")
    private String operatorId;
    private Long time;
    @JSONField(name = "self_id")
    private String selfId;
    @JSONField(name = "sub_type")
    private String subType;
    @JSONField(name = "user_id")
    private String userId;
    private Map<String,String> sender;
    @JSONField(name = "group_id")
    private String groupId;
    @JSONField(name = "target_id")
    private String targetId;
    private String message;
    @JSONField(name = "raw_message")
    private String rawMessage;
    private Integer font;
    //private Sender sender;
    @JSONField(name = "message_id")
    private String messageId;
    @JSONField(name = "message_seq")
    private Integer messageSeq;
    private String anonymous;
}