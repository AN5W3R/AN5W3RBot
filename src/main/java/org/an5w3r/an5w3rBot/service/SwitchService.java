package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.Setting;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.util.JSONUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwitchService {
    public static List<String> functionList ;

    static {
        functionList = new ArrayList<>();
        functionList.add("涩图");
        functionList.add("美图");
    }

    public static void changeFunction(Message parseObject, String[] str) throws IOException {
        if (str.length != 4){
            MsgAction.sendMsg(parseObject,MsgService.msgTipsText("命令格式不正确\n正确格式为:\n"+ JSONUtil.getSettingMap().get("identifier")+"功能管理 功能名 开启/关闭"));
            return;
        }

        if (isFunction(str[2])) {
            if("开启".equals(str[3])){
                openFunction(parseObject,str[2]);
            } else if ("关闭".equals(str[3])) {
                closeFunction(parseObject,str[2]);
            }
        }else {
            MsgAction.sendMsg(parseObject,MsgService.msgTipsText("功能不存在"));
        }
    }

    public static boolean isFunction(String functionName){//判断功能是否存在
        return functionList.contains(functionName);
    }

    public static boolean isFunctionOn(Message parseObject, String functionName) throws IOException {
        String groupId = parseObject.getGroupId();

        if (Setting.FunctionSwitch.containsKey(groupId)) {//判断Map中是否存在群号
            List<String> groupOffFunctions = Setting.FunctionSwitch.get(groupId);
            if (groupOffFunctions.contains(functionName)) {//Map中有对应功能,为关闭
                MsgAction.sendMsg(parseObject,MsgService.msgTipsText(functionName+"功能已关闭"));
                return false;
            }
            return true;//Map中没有对应功能,为打开
        } else {//Map中群号时向里面添加
            List<String> groupOffFunctions = new ArrayList<>();
            Setting.FunctionSwitch.put(groupId,groupOffFunctions);
            return true;
        }

    }

    public static void openFunction(Message parseObject, String functionName) throws IOException {
        String groupId = parseObject.getGroupId();

        if (isFunctionOn(parseObject,functionName)) {
            MsgAction.sendMsg(parseObject,MsgService.msgTipsText("功能已经是开启状态了"));
        } else {
            MsgAction.sendMsg(parseObject,MsgService.msgTipsText("功能已开启"));
            Setting.FunctionSwitch.get(groupId).remove(functionName);
        }

    }

    public static void closeFunction(Message parseObject, String functionName) throws IOException {
        String groupId = parseObject.getGroupId();

        if (!isFunctionOn(parseObject,functionName)) {
            MsgAction.sendMsg(parseObject,MsgService.msgTipsText("功能已经是关闭状态了"));
        } else {
            MsgAction.sendMsg(parseObject,MsgService.msgTipsText("功能已关闭"));
            Setting.FunctionSwitch.get(groupId).add(functionName);
        }
    }
}
