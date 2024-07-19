package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.dao.MsgDao;
import org.an5w3r.an5w3rBot.entity.MsgItem;

import java.io.IOException;
import java.util.ArrayList;

public class MsgService {//这里写具体如何回复
    public static MsgItem tipsText(String message) throws IOException {
        MsgItem item = new MsgItem("text","text", message);
        return item;
    }

    public static MsgItem oneText(String message) throws IOException {
        MsgItem item = new MsgItem("text","text", MsgDao.getTextByMsg(message));
        return item;
    }


    public static MsgItem translationText(String[] msgStr){//#翻译 文本 目标语言 源语言
        MsgItem item = new MsgItem("text","text",MsgDao.getTranslation(msgStr));
        return item;
    }

    public static MsgItem randomImage(String message) throws IOException {
        MsgItem item = new MsgItem("image","file", MsgDao.getImageByMsg(message));
        return item;
    }

    public static MsgItem atQQ(String QQ){
        MsgItem item = new MsgItem("at", "qq", QQ);
        return item;
    }
}
