package org.an5w3r.an5w3rBot.util;

import org.an5w3r.an5w3rBot.entity.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;

public class ImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    //判断是否是图片
    public static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp" };
        String fileName = file.getName().toLowerCase();
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    public static String getRandomImageUrl(){
//        https://api.sevin.cn/api/ecy.php
        StringBuilder content = new StringBuilder();
        try {
            // 创建URL对象
            URL url = new URL("https://api.sevin.cn/api/ecy.php");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 读取响应内容
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // 关闭流
            in.close();

            // 打印响应内容
            logger.info("Response Content: " + content.toString());
        } catch (Exception e) {
            return "请求时出了点小问题";
        }
        return content.toString();
    }

    //截取图片部分
    public static String cropImage(String imagePath) throws IOException {
        //TODO 添加判断截取后图片大小
        BufferedImage image = ImageIO.read(new File(imagePath.replaceFirst("file:///","")));

        int width = image.getWidth();
        int height = image.getHeight();
        double guessRatio = Double.parseDouble(JSONUtil.getSettingMap().get("guessRatio"));
        int newWidth = (int) (width / guessRatio);
        int newHeight = (int) (height / guessRatio);

        Random rand = new Random();
        int x = rand.nextInt(width - newWidth);
        int y = rand.nextInt(height - newHeight);

        BufferedImage croppedImage = image.getSubimage(x, y, newWidth, newHeight);

        return bufferedImageToBase64(croppedImage);
    }
    //压缩图片并转为Base64
    private static String compressImage(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(quality); // 设置压缩质量

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            jpgWriter.setOutput(ios);
            jpgWriter.write(null, new IIOImage(image, null, null), jpgWriteParam);
        }
        jpgWriter.dispose();
//        return baos.toByteArray();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    //文件转Base64
    private static String encodeFileToBase64Binary(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        return Base64.getEncoder().encodeToString(bytes);
    }
    // 将Base64字符串转换为BufferedImage
    public static BufferedImage base64ToBufferedImage(String base64Image) throws IOException {
        base64Image =  base64Image.replaceFirst("base64://","");
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bis);
    }
    // 将BufferedImage转换为Base64字符串
    public static String bufferedImageToBase64(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpeg", bos);
            byte[] imageBytes = bos.toByteArray();
            return "base64://" + Base64.getEncoder().encodeToString(imageBytes);
        }
    }

//    public static String bufferedImageToBase64(BufferedImage image) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", bos);
//        byte[] imageBytes = bos.toByteArray();
//        return "base64://"+Base64.getEncoder().encodeToString(imageBytes);
//    }



}

