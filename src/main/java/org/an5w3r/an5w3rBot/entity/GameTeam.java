package org.an5w3r.an5w3rBot.entity;

import lombok.Data;
import org.an5w3r.an5w3rBot.action.MsgAction;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameTeam {
    String name;//队伍名称
    Message parseObject;//组建信息
    Integer maxCount;//最大人数
    //当前人数用List.size
    String text;//自定义文本
    List<String> userIdList;//队伍中人员的QQ号,用于@

    public void atList(){
        List<MsgItem> itemList = new ArrayList<>();
        for (String s : userIdList) {
            itemList.add(MsgItem.atItem(s));
        }
        MsgAction.sendMsg(parseObject,itemList);

    }

    public void add(String userId){
        userIdList.add(userId);

    }

}
