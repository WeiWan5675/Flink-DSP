package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.core.pub.SystemEnvManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(value = "/websocket/application/logs/{jobId}")
@Component
@Slf4j
public class ApplicationLogWebSocket {

    /**
     * 连接集合
     */
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();
    private static final String LOG_FILE_NAME_FORMAT = "job-%s.log";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("jobId") final String jobId) {
        sessionMap.put(session.getId(), session);
        String logDir = SystemEnvManager.getInstance().getDspLogDir();
        String logFile = logDir + File.separator + "jobs" + File.separator + String.format(LOG_FILE_NAME_FORMAT, jobId);
        new SendLogThread(session, sessionMap, logFile).start();
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        //从集合中删除
        sessionMap.remove(session.getId());
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }



    class SendLogThread extends Thread {
        private int lastLineIndex = 0;
        private Session session;
        private String logFile;
        private Map<String, Session> sessionMap;
        private final Logger logger = LoggerFactory.getLogger(SendLogThread.class);
        public SendLogThread(Session session, Map<String, Session> sessionMap, String logFile) {
            this.session = session;
            this.sessionMap = sessionMap;
            this.logFile = logFile;
        }

        @Override
        public void run() {
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            logger.info("start push application logs, logFile: {}, sessionId: {}", logFile, session.getId());
            while (sessionMap.get(session.getId()) != null) {
                try {
                    File file = new File(logFile);
                    if(file.exists()){
                        fileReader = new FileReader(logFile);
                        bufferedReader = new BufferedReader(fileReader);
                    }
                    Object[] lines = bufferedReader.lines().toArray();
                    Object[] copyOfRange = lines;
                    if(lastLineIndex != 0) {
                        copyOfRange = Arrays.copyOfRange(lines, lastLineIndex, lines.length);
                    }
                    if (lastLineIndex == 0 && copyOfRange.length > 1000) {
                        copyOfRange = Arrays.copyOfRange(copyOfRange, copyOfRange.length - 1000, copyOfRange.length);
                    }

                    lastLineIndex = lines.length == 0 ? 0 : lines.length;
                    String result = StringUtils.join(copyOfRange, "<br/>");
                    session.getBasicRemote().sendText(result);
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (Exception exception) {
                } finally {
                    try {
                        if(fileReader != null)
                            fileReader.close();
                        if(bufferedReader != null)
                            bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            logger.info("end of push application logs, logFile: {}, sessionId: {}", logFile, session.getId());
        }
    }
}