package org.an5w3r.an5w3rBot.service;

import org.an5w3r.an5w3rBot.action.MsgAction;
import org.an5w3r.an5w3rBot.entity.Message;
import org.an5w3r.an5w3rBot.entity.MsgItem;
import org.an5w3r.an5w3rBot.entity.Sender;
import org.an5w3r.an5w3rBot.entity.Team;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class GameTeamService {
    public static Map<String, Map<String, Team>> groupMap = new HashMap<>(); // 用来存储不同群中的队伍列表，第一个key是群id，第二个是队伍名称

    private static final Map<String, ScheduledFuture<?>> teamTasks = new ConcurrentHashMap<>();//多线程队伍
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(
            CORE_POOL_SIZE,
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    static {
        ((ScheduledThreadPoolExecutor) scheduler).setMaximumPoolSize(MAX_POOL_SIZE);
        ((ScheduledThreadPoolExecutor) scheduler).setKeepAliveTime(KEEP_ALIVE_TIME, TimeUnit.SECONDS);
    }

    private static void startScheduledTask(Team team) {
        String teamId = team.getName();
        if (!teamTasks.containsKey(teamId) || teamTasks.get(teamId).isCancelled()) {
            ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(() -> {
                try {
                    GameTeamService.promptTeam(team);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 1, 1, TimeUnit.MINUTES);
            teamTasks.put(teamId, scheduledTask);
        }
    }

    private static void stopScheduledTask(String teamId) {
        if (teamTasks.containsKey(teamId) && !teamTasks.get(teamId).isCancelled()) {
            teamTasks.get(teamId).cancel(true);
            teamTasks.remove(teamId);
        }
    }



    static boolean existTeam(String teamName, String groupId) {
        if (groupMap.containsKey(groupId)) {
            return groupMap.get(groupId).containsKey(teamName);
        }
        groupMap.put(groupId, new HashMap<String, Team>());
        return false;
    }

    public static void addTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();
        // 判断格式是否正确
        if (splitMsg.length < 4) { // 格式不正确
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        // 判断队伍是否存在
        if (existTeam(splitMsg[1], message.getGroupId())) { // 队伍已存在
            MsgAction.sendMsg(message, new MsgItem("队伍已存在"));
            return;
        }
        // 新建队伍注入信息
        Team team = new Team();
        team.setName(splitMsg[1]);
        team.setMessage(message);
        Integer maxCount = Integer.valueOf(splitMsg[2]);
        if (maxCount > 1) {
            team.setMaxCount(maxCount);
        } else {
            MsgAction.sendMsg(message, new MsgItem("人数必须大于1人"));
            return;
        }

        team.setText(splitMsg[3]);
        List<Sender> senderList = new ArrayList<>();
        team.setSenderList(senderList);
        team.join(message);

        groupMap.get(message.getGroupId()).put(splitMsg[1], team); // 加入群内队伍列表

        // 提示创建成功
        MsgAction.sendMsg(message, new MsgItem("创建队伍成功\n" + team));

        // 启动定时任务
        startScheduledTask(team);
    }

    public static void joinTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        if (!existTeam(splitMsg[1], message.getGroupId())) {
            MsgAction.sendMsg(message, new MsgItem("队伍不存在"));
            return;
        }
        // 获取队伍
        Team team = groupMap.get(message.getGroupId()).get(splitMsg[1]);
        team.join(message);

        MsgAction.sendMsg(message, new MsgItem(team.toString()));
        if (team.getSenderList().size() == team.getMaxCount()) {
            playTeam(message);
        }
    }

    public static void leaveTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        if (!existTeam(splitMsg[1], message.getGroupId())) {
            MsgAction.sendMsg(message, new MsgItem("队伍不存在"));
            return;
        }
        // 获取队伍
        Team team = groupMap.get(message.getGroupId()).get(splitMsg[1]);
        team.leave(message);

        MsgAction.sendMsg(message, new MsgItem(team.toString()));
        if (team.getSenderList().isEmpty()) {
            removeTeam(message);
        }
    }

    public static void removeTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        if (!groupMap.get(message.getGroupId()).containsKey(splitMsg[1])) {
            MsgAction.sendMsg(message, new MsgItem("队伍不存在"));
            return;
        }

        groupMap.get(message.getGroupId()).remove(splitMsg[1]);
        MsgAction.sendMsg(message, new MsgItem("队伍" + splitMsg[1] + "已解散"));

        // 停止定时任务
        stopScheduledTask(splitMsg[1]);
    }

    public static void playTeam(Message message) throws IOException {
        String[] splitMsg = message.splitMsg();

        if (splitMsg.length < 2) {
            MsgAction.sendMsg(message, new MsgItem("指令格式不正确"));
            return;
        }
        if (!groupMap.get(message.getGroupId()).containsKey(splitMsg[1])) {
            MsgAction.sendMsg(message, new MsgItem("队伍不存在"));
            return;
        }
        Team team = groupMap.get(message.getGroupId()).get(splitMsg[1]);
        team.go();

        groupMap.get(message.getGroupId()).remove(splitMsg[1]);

        // 停止定时任务
        stopScheduledTask(splitMsg[1]);
    }

    public static void promptTeam(Team team) throws IOException { // 队伍提醒
        MsgAction.sendMsg(team.getMessage(), new MsgItem(team.toString()));
    }

    public static void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("线程池未能正常关闭");
            }
        } catch (InterruptedException ie) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
