package org.an5w3r.an5w3rBot;

import org.an5w3r.an5w3rBot.util.JSONUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AddTextToImage {
    public static void main(String[] args) {
        try {
            // 读取图片
            BufferedImage image = ImageIO.read(new File("D:\\QQBOT\\AN5W3RBot\\fortune\\frame_1.jpg"));

            // 获取Graphics2D对象
            Graphics2D g2d = image.createGraphics();

            // 设置字体和颜色

            String title = "大吉";

            org.an5w3r.an5w3rBot.entity.Font font = JSONUtil.getFont();
            int titleX = font.getTitleX();
            int titleY= font.getTitleY();
            int titleStyle = font.getTitleStyle();
            int titleSize = font.getTitleSize();
            String titleFont = font.getTitleFont();
            Color titleColor = font.titleColor();

            int textX = font.getTextX();
            int textY = font.getTextY();
            int textStyle = font.getTextStyle();
            int textSize = font.getTextSize();
            String textFont = font.getTextFont();
            int lineSize = font.getLineSize();
            Color textColor = font.textColor();


            g2d.setFont(new Font(titleFont,titleStyle,  titleSize));
            g2d.setColor(titleColor);
            g2d.drawString(title, titleX, titleY);


            g2d.setFont(new Font(textFont, textStyle, textSize));
            g2d.setColor(textColor);
            // 绘制竖向文本
            String text = "曾经的努力和经验会成为他人眼中魅力的样子";

            for (int i = 0; i < text.length(); i++) {
                if (i%lineSize==0){
                    textX +=textSize;
                }
                g2d.drawString(String.valueOf(text.charAt(i)), textX, textY + (i%lineSize * textSize));
            }

            // 释放Graphics2D对象
            g2d.dispose();

            // 保存修改后的图片
            ImageIO.write(image, "jpg", new File("src/test/output_image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
