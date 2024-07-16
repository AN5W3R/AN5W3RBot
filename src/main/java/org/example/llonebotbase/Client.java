package org.example.llonebotbase;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.example.llonebotbase.dao.MsgDao;
import org.example.llonebotbase.entity.Message;
import org.example.llonebotbase.entity.MsgItem;
import org.example.llonebotbase.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息监听
 */
@ClientEndpoint
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Session session;
    public static Client instance;
    public static boolean isOpen = false;
 
    private Client(String url) {
        try {
            session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public synchronized static boolean connect(String url) {
        instance = new Client(url);
        return true;
    }
 
    @OnOpen
    public void onOpen(Session session) {
        isOpen = true;
        logger.info("连接成功！");
    }
 
    @OnClose
    public void onClose(Session session) {
        isOpen = false;
        logger.info("连接关闭！");
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("连接错误！");
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
//        System.out.println("\n"+message);
        if (message.contains("\"post_type\":\"message\"")) {//处理message信息
            Message parseObject = JSONObject.parseObject(message, Message.class);//JSON转换为对象

            if("private".equals(parseObject.getMessageType())){//私聊信息
                sendMsg(parseObject, parseObject.getMessageType());
            }
            if ("group".equals(parseObject.getMessageType())) {//群聊信息
                if(parseObject.getRawMessage().contains("[CQ:at,qq="+parseObject.getSelfId()+"]")){//被@的情况message
                    sendMsg(parseObject, parseObject.getMessageType());
                }
            }
        }
    }
    /**
     * 消息
     */
    public synchronized static void sendMsg(Message parseObject,String detailType) throws IOException {
        String message = parseObject.getRawMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("detail_type",detailType);

        if("private".equals(detailType)){
            logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);
            params.put("user_id", parseObject.getUserId());
        } else if("group".equals(detailType)){
            logger.info("收到群" + parseObject.getGroupId() + "的消息：" + message);
            params.put("group_id", parseObject.getGroupId());//设置要发送的群
        } else {
            return;
        }

//TODO        获取msgList的逻辑可以写在service
        ArrayList<MsgItem> msgList = new ArrayList<>();
        MsgItem item = new MsgItem("text","text",MsgDao.getTextByMsg(message));
        msgList.add(item);
//        MsgItem item1 = new MsgItem("image","file","https://gchat.qpic.cn/gchatpic_new/1542338612/758025242-3108042462-E04E0D37BB15FF418729C4AE9118A180/0?term=255&is_origin=0");
//        msgList.add(item1);

        params.put("message",msgList);
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_msg");
        paramsRequest.setParams(params);

        String msg = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        instance.session.getAsyncRemote().sendText(msg);//发出请求
    }


}

