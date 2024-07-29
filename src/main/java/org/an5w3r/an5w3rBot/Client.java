package org.an5w3r.an5w3rBot;

import com.alibaba.fastjson.JSONObject;
import jakarta.websocket.*;
import org.an5w3r.an5w3rBot.action.GroupAction;
import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.dao.ImageDao;
import org.an5w3r.an5w3rBot.dao.TextDao;
import org.an5w3r.an5w3rBot.entity.Image;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.service.GameTeamService;
import org.an5w3r.an5w3rBot.service.MessageService;
import org.an5w3r.an5w3rBot.service.SwitchService;
import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 消息监听
 */
@ClientEndpoint
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    public Session session;
    public static Client instance;
 
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
        Setting.isOpen = true;
        logger.info("连接成功！");
    }
 
    @OnClose
    public void onClose(Session session) {
        Setting.isOpen = false;
        logger.info("连接关闭！");
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("连接错误！");
    }


    private final ExecutorService executorService = Executors.newFixedThreadPool(12); // 线程池大小可以根据需要调整

    @OnMessage
    public void onMessage(String messageStr) throws IOException, ExecutionException, InterruptedException {
        if (messageStr.contains("\"post_type\":\"message\"")) {
            executorService.submit(() -> {
                try {
                    MessageService.handleMessage(messageStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("线程池未能正常关闭");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


}

