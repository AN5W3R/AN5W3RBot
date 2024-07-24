package org.an5w3r.an5w3rBot.entity;

import lombok.Data;
import org.an5w3r.an5w3rBot.action.MsgAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Team {
    String name;//队伍名称
    Message parseObject;//组建信息
    Integer maxCount;//最大人数
    //当前人数用List.size
    String text;//自定义文本
    List<Map<String,String>> senderList;//队伍中人员的QQ号,用于@

    public void go(){
        List<MsgItem> itemList = new ArrayList<>();
        for (Map<String, String> sender : senderList) {
            itemList.add(MsgItem.atItem(sender.get("user_id")));
        }
        itemList.add(new MsgItem("\n"+name+":"+text+"\n启动!!!"));
        MsgAction.sendMsg(parseObject,itemList);
    }

    public void join(Message parseObject) throws IOException {
        if (senderList.contains(parseObject.getSender())) {
            MsgAction.sendMsg(parseObject,new MsgItem("已在队伍中"));
            return;
        }
        MsgAction.sendMsg(parseObject, new MsgItem("加入队伍成功"));
        senderList.add(parseObject.getSender());
    }
    public void leave(Message parseObject) throws IOException {
        if (senderList.contains(parseObject.getSender())){
            senderList.remove(parseObject.getSender());
            MsgAction.sendMsg(parseObject,new MsgItem("退出队伍成功"));
        } else {//不在队伍中
            MsgAction.sendMsg(parseObject,new MsgItem("不在队伍中"));
        }
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(
                "队伍名称:"+name+
                        "\n当前人数:" +senderList.size()+"/"+maxCount+
                        "\n" +text+
                        "\n人员名单:\n");
        for (Map<String, String> sender : senderList) {
            if (sender.get("card").isBlank()){
                ret.append(sender.get("nickname")+"\n");
            } else {
                ret.append(sender.get("card")+"\n");
            }
        }
        return ret.toString();
    }
}
