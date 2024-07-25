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
    Message message;//组建信息
    Integer maxCount;//最大人数
    //当前人数用List.size
    String text;//自定义文本
    List<Sender> senderList;//队伍中人员的QQ号,用于@

    public void go(){
        List<MsgItem> itemList = new ArrayList<>();
        for (Sender sender : senderList) {
            itemList.add(MsgItem.atItem(sender.getUserId()));
        }
        itemList.add(new MsgItem("\n"+name+":"+text+"\n启动!!!"));
        MsgAction.sendMsg(message,itemList);
    }

    public void join(Message message) throws IOException {
        if (senderList.contains(message.getSender())) {
            MsgAction.sendMsg(message,new MsgItem("已在队伍中"));
            return;
        }
        senderList.add(message.getSender());
        if (senderList.size()>1){
            MsgAction.sendMsg(message, new MsgItem("加入队伍成功"));
        }

    }
    public void leave(Message message) throws IOException {
        if (senderList.contains(message.getSender())){
            senderList.remove(message.getSender());
            MsgAction.sendMsg(message,new MsgItem("退出队伍成功"));
        } else {//不在队伍中
            MsgAction.sendMsg(message,new MsgItem("不在队伍中"));
        }
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(
                "队伍名称:"+name+
                "\n备注:" +text+
                "\n当前人数:" +senderList.size()+"/"+maxCount+
                "\n人员名单:");
        for (Sender sender : senderList) {
            if (sender.getCard().isBlank()){
                ret.append("\n"+sender.getNickname());
            } else {
                ret.append("\n"+sender.getCard());
            }
        }
        return ret.toString();
    }
}
