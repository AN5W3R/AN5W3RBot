package org.an5w3r.an5w3rBot.entity;

import lombok.Data;

import java.awt.*;

@Data
public class Font {
    int titleX;
    int titleY;
    int titleStyle;
    int titleSize;
    String titleFont;
    String titleColor;

    int textX;
    int textY;
    int textStyle;
    int textSize;
    String textFont;
    String textColor;

    int lineSize;
    public Color textColor() {
        return Color.decode(textColor);
    }
    public Color titleColor() {
        return Color.decode(titleColor);
    }
}
