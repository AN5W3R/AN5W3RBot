package unUse;

public class unUseOnMsg {
    //    @OnMessage
//    public void onMessage(String messageStr) throws IOException, ExecutionException, InterruptedException {
////        System.out.println("\n"+messageStr);
//        if (messageStr.contains("\"post_type\":\"message\"")) {//处理message信息
//            Message message = JSONObject.parseObject(messageStr, Message.class);//JSON转换为对象
//
//            if("private".equals(message.getMessageType())){//私聊信息
//                logger.info("\n收到好友[" + message.getSender().getNickname()
//                        + "]的消息：" + message.getRawMessage()+"\n");
//                MsgAction.sendMsg(message
//                        ,new MsgItem(TextDao.getAtTextByMsg(message)));
//            }
//            if ("group".equals(message.getMessageType())) {//群聊信息
//                logger.info("\n收到群" + message.getGroupId()+"-[" + message.getSender().getCard()
//                        + "]的消息：" + message.getRawMessage()+"\n");
////                System.out.println(message.getSender());
////                if (message.getSender().getUserId().equals("2468794766")){//小莫说话
////                    MsgAction.deleteMsg(message);
////                    GroupAction.setGroupCard(message,"");
////                    GroupAction.setGroupBan(message,1);
////                }
//
//                //被@
//                if(message.getRawMessage().contains("[CQ:at,qq="+message.getSelfId()+"]")){
//
//
//                    if(message.getRawMessage().contains(JSONUtil.getSettingMap().get("identifier"))){ //调用功能
//                        boolean flag = true;
//                        String[] splitMsg = message.splitMsg();
//                        //功能管理
//                        if(splitMsg[0].contains("功能管理")){
//                            flag=false;
//                            if ("owner".equals(message.getSender().getRole())||"admin".equals(message.getSender().getRole())){
//                                SwitchService.changeFunction(message);
//                            } else {
//                                MsgAction.sendMsg(message
//                                        ,MsgItem.atItem(message.getUserId())
//                                        ,new MsgItem("你没有管理员权限"));
//                            }
//                        }
//
//                        //内置功能系统
//                        if(flag){
//                            for (String key : SwitchService.functionList) {//查找功能列表中是否有此功能
//                                if (splitMsg[0].contains(key)) {
//                                    flag=false;
//                                    if (SwitchService.isFunctionOn(message,key)) {
//                                        switch (key){//功能列表
//                                            case "翻译":{
//                                                MsgAction.sendMsg(message
//                                                        ,MsgItem.atItem(message.getUserId())
//                                                        ,new MsgItem(TextDao.getTranslation(message)));
//                                                break;
//                                            }
//                                            case "创建":{
//                                                GameTeamService.addTeam(message);
//                                                break;
//                                            }
//                                            case "加入":{
//                                                GameTeamService.joinTeam(message);
//                                                break;
//                                            }
//                                            case "退出":{
//                                                GameTeamService.leaveTeam(message);
//                                                break;
//                                            }
//                                            case "解散":{
//                                                GameTeamService.removeTeam(message);
//                                                break;
//                                            }
//                                            case "开了":{
//                                                GameTeamService.playTeam(message);
//                                                break;
//                                            }
//                                        }
//                                    } else {
//                                        MsgAction.sendMsg(message
//                                                ,MsgItem.atItem(message.getUserId())
//                                                ,new MsgItem(key+"功能已被关闭"));
//                                    }
//
//                                }
//                            }
//                        }
//                        //图库功能系统
//                        if (flag){
//                            Map<String, String> imageFunctionMap = JSONUtil.getImageSrcMap();
//                            for (String key : imageFunctionMap.keySet()) {//查找图库功能列表
//                                if (splitMsg[0].contains(key)) {
//                                    if (SwitchService.isFunctionOn(message,key)) {
//                                        Image image = ImageDao.getImageByMsg(key);
//                                        MsgAction.sendMsg(message
//                                                ,MsgItem.atItem(message.getUserId())
//                                                ,new MsgItem("image","file", image.getFile())
//                                                ,new MsgItem(image.getText())
//                                        );//参数即是功能名
//                                        break;
//                                    } else {
//                                        MsgAction.sendMsg(message
//                                                ,MsgItem.atItem(message.getSender().getUserId())
//                                                ,new MsgItem(key+"功能已被关闭"));
//                                        break;
//                                    }
//
//                                }
//                            }
//                        }
//                    } else {
//                        MsgAction.sendMsg(message
//                                ,MsgItem.atItem(message.getSender().getUserId())
//                                ,new MsgItem(TextDao.getAtTextByMsg(message)));
//                    }
//
//                }else {//无@对话
//                    Map<String, String[]> map = JSONUtil.getNotAtTextMap();
//
//                    Set<String> keys = map.keySet();
//                    for (String key : keys) {//先判断在notAtTextMap中是否有符合key正则表达式
//                        String noAtMsg = message.atMsg();
//                        if (Pattern.matches(key,noAtMsg)) {
//                            MsgAction.sendMsg(message,new MsgItem(TextDao.getNotAtTextByMsg(key)));
//                            MsgAction.deleteMsg(message);
////                            GroupAction.setGroupCard(message,"");
//                            GroupAction.setGroupBan(message,1);
//                            break;
//                        }
////                        if (message.getRawMessage().contains(key)) {
////                            MsgAction.sendMsg(message,new MsgItem(TextDao.getNotAtTextByMsg(key)));
////                        }
//                    }
//                }
//
//            }
//        }
//    }
}
