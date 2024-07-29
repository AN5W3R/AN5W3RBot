package org.an5w3r.an5w3rBot.dao;

import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.TextUtil;

import java.io.IOException;

public class ImageDao {
    //接收一个参数 图库名称,来源外部或聊天输入
    //获得的除了文件本身,还有对应文本
    public static Image getImageByMsg(String in) throws IOException {
        //根据JSON图库名称获取路径
        String src = JSONUtil.getImageSrcMap().get(in);

        Image randomImageLocal = ImageUtil.getRandomImageLocal(src);
        randomImageLocal.setType(in);
        randomImageLocal.setTexts(TextUtil.getTextsByImage(randomImageLocal));
        randomImageLocal.setText(TextUtil.getTextByImage(randomImageLocal));

        return randomImageLocal;
    }
}
