package org.an5w3r.an5w3rBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class AN5W3RBotApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SpringApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AN5W3RBotApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Client.connect("ws://127.0.0.1:6700");
        // 断线重连
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Status.isOpen || Client.instance == null) {
                    Client.connect("ws://127.0.0.1:6700");
                    logger.error("正在断线重连！");
                }
            }
        }, 1000, 5000);
    }
}
