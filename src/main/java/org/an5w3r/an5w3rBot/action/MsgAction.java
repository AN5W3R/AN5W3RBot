package org.an5w3r.an5w3rBot.action;

import com.alibaba.fastjson.JSONObject;
import org.an5w3r.an5w3rBot.Client;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.Request;
import org.an5w3r.an5w3rBot.service.MsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MsgAction {
    private static final Logger logger = LoggerFactory.getLogger(MsgAction.class);

    public synchronized static void sendMsg(Message parseObject, String detailType) throws IOException {
        String message = parseObject.getRawMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("detail_type",detailType);
        if("private".equals(detailType)){
            logger.info("收到好友" + parseObject.getUserId() + "的消息：" + message);
            params.put("user_id", parseObject.getUserId());
        } else if("group".equals(detailType)){
            logger.info("收到群" + parseObject.getGroupId() + "的消息：" + message);
            params.put("group_id", parseObject.getGroupId());
        } else {
            return;
        }

        ArrayList<MsgItem> RetMessage = MsgService.msgListDefault(message);//这里要使用不同的方法来决定不同的发生内容
        params.put("message",RetMessage);
        Request<Object> paramsRequest = new Request<>();
        paramsRequest.setAction("send_msg");
        paramsRequest.setParams(params);

        String msg = JSONObject.toJSONString(paramsRequest);//将请求转换为json
        Client.instance.session.getAsyncRemote().sendText(msg);//发出请求
    }
}
