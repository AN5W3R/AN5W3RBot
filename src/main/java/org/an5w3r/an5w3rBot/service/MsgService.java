package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.dao.MsgDao;
import org.an5w3r.an5w3rBot.entity.MsgItem;

import java.io.IOException;
import java.util.ArrayList;

public class MsgService {//这里写具体如何回复
    public static ArrayList<MsgItem> msgTipsText(String message) throws IOException {
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("text","text", message);
        msgList.add(item);
        return msgList;
    }

    public static ArrayList<MsgItem> msgOneText(String message) throws IOException {
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("text","text", MsgDao.getTextByMsg(message));
        msgList.add(item);
//        MsgItem item1 = new MsgItem("image","file","https://gchat.qpic.cn/gchatpic_new/1542338612/758025242-3108042462-E04E0D37BB15FF418729C4AE9118A180/0?term=255&is_origin=0");
//        msgList.add(item1);
        return msgList;
    }

    public static ArrayList<MsgItem> msgOneRandomImage(String message) throws IOException {
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("image","file", MsgDao.getImageByMsg(message));
        msgList.add(item);
        return msgList;
    }

}
