package org.an5w3r.an5w3rBot.dao;

import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.util.ImageUtil;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.an5w3r.an5w3rBot.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageDao {

    public static Image getImageByMsg(String in) throws IOException {
        // 根据JSON图库名称获取路径
        String src = JSONUtil.getImageSrcMap().get(in);
        if (src==null || src.isEmpty()){
            return null;
        }
        Image retImg = new Image();
        File folder = new File(src);

        List<String> imagePaths = new ArrayList<>();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (ImageUtil.isImageFile(file)) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }

        if (!imagePaths.isEmpty()) {
            Random random = new Random(System.currentTimeMillis());
            String randomImagePath = imagePaths.get(random.nextInt(imagePaths.size()));
            File file = new File(randomImagePath);
            retImg.setFile("file:///" + file.getAbsolutePath());
            retImg.setFileName(file.getName());
        } else {
            retImg.setFileName("FromURL");
            retImg.setFile(ImageUtil.getRandomImageUrl());
            retImg.setText("图片来源网络");
            retImg.setType("网络图片");
        }

        retImg.setType(in);
        retImg.setTexts(TextUtil.getTextsByImage(retImg));
        retImg.setText(TextUtil.getTextByImage(retImg));

        return retImg;
    }

    public static Image getFortuneImage() throws IOException {//获取底图
        Image image = new Image();

        String src = JSONUtil.getSettingMap().get("fortuneSrc") ;
        File folder = new File(src);
        List<String> imagePaths = new ArrayList<>();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (ImageUtil.isImageFile(file)) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }
        if (!imagePaths.isEmpty()) {
            Random random = new Random(System.currentTimeMillis());
            String randomImagePath = imagePaths.get(random.nextInt(imagePaths.size()));
            File file = new File(randomImagePath);
            image.setFile("file:///" + file.getAbsolutePath());
        } else {//获取底图失败

        }

        return image;
    }
}
