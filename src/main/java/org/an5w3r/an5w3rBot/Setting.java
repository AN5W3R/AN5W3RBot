package org.an5w3r.an5w3rBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Setting {
    public static boolean isOpen = false;
    public static Map<String, List<String>> FunctionSwitch  = new HashMap<>();//默认为全开,key为群号,value为要关闭的功能
}
