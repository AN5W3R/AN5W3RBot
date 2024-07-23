package org.an5w3r.an5w3rBot.entity;

import lombok.Data;

@Data
public class Image {
    private String fileName;//文件名
    private String file;//文件本体
    private String text;//对应文本
    private String type;//图库名称
}
