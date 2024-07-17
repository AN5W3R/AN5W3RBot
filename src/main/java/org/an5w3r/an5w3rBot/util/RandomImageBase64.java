package org.an5w3r.an5w3rBot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class RandomImageBase64 {
    public static void main(String[] args) {
        String folderPath = "D:\\QQBOT\\Images";
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
                writeBase64ToFile(base64Image, "base64.txt");
                System.out.println("Base64 Encoded Image written to base64.txt");//这里换return
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No images found in the specified folder.");
        }
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

    private static void writeBase64ToFile(String base64Content, String fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(base64Content.getBytes());
        }
    }
}
