package org.an5w3r.an5w3rBot;

import org.an5w3r.an5w3rBot.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class AN5W3RBotApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SpringApplication.class);
    public static final String URL;

    static {
        try {
            URL = "ws://127.0.0.1:"+ JSONUtil.getSettingMap().get("port");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AN5W3RBotApplication.class, args);
    }


    @Override
    public void run(String... args) {
        logger.info("正在连接"+URL);
        Client.connect(URL);

        // 断线重连
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Setting.isOpen || Client.instance == null) {
                    logger.error("正在断线重连！"+URL);
                    Client.connect(URL);
                }
            }
        }, 1000, 5000);

        t.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        },1000,5000);
    }
}
