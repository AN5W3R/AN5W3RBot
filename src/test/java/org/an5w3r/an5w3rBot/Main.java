package org.an5w3r.an5w3rBot;

public class Main {
    public static void main(String[] args) {
        String input = "[CQ:at,qq=3363590760,name=ANSWER] 你好";
        String output = input.replaceFirst("\\[.*?]", "");
        System.out.println(output); // 输出 "你好"
    }
}
