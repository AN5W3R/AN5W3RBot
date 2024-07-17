package org.an5w3r.an5w3rBot.util;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class ImageUtil {
    public static String getRandomImageLocal() throws IOException {
        String folderPath = JSONUtil.getSettingMap().get("ImageSrc");
        File folder = new File(folderPath);
        List<String> imagePaths = new ArrayList<>();

        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (isImageFile(file)) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }

        if (!imagePaths.isEmpty()) {
            Random random = new Random();
            String randomImagePath = imagePaths.get(random.nextInt(imagePaths.size()));
            try {
                String base64Image = encodeFileToBase64Binary(randomImagePath);
                return "base64://"+base64Image;//这里换return
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return getRandomImageUrl();
        }
        return getRandomImageUrl();
    }

    private static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp" };
        String fileName = file.getName().toLowerCase();
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private static String encodeFileToBase64Binary(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        return Base64.getEncoder().encodeToString(bytes);
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
            System.out.println("Response Content: " + content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}

