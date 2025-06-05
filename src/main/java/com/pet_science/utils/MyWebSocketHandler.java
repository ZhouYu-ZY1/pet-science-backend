package com.pet_science.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.pojo.user.User;
import com.pet_science.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final UserService userService;
    public MyWebSocketHandler(UserService userService){
        this.userService = userService;
    }

    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();

        // 提取查询参数
        String query = uri.getQuery();  // 获取 query string: token=${token}&id=${id}

        if (query != null) {
            Map<String, String> params = parseQueryParams(query);
            String token = params.get("token");
            String userId = String.valueOf(JWTUtil.getUserId(token));

            session.getAttributes().put("id", userId);
            sessions.put(userId, session);
        }else {
            sessions.put(session.getId(), session);
        }
        System.out.println("WebSocket连接已建立，当前用户数：" + sessions.size());
    }


    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        // 收到消息时调用
        String payload = message.getPayload();

        JSONObject jsonObject = JSON.parseObject(payload);
        String receiverId = jsonObject.getString("receiverId"); //接收者ID
        String senderId = jsonObject.getString("senderId"); //发送者ID

        Integer userId = JWTUtil.getUserId(senderId);
        senderId = String.valueOf(userId);
        jsonObject.put("senderId",senderId);  //获取发送者ID
        jsonObject.put("timestamp",System.currentTimeMillis()); //设置发送时间

        User userInfo = userService.findUserById(userId);
        jsonObject.put("userInfo",userInfo);

        try {
            WebSocketSession webSocketSession = sessions.get(receiverId); //获取接收者session
            if(webSocketSession != null){
                webSocketSession.sendMessage(new TextMessage(jsonObject.toJSONString()));
                System.out.println("发送消息成功："+jsonObject.toJSONString());
            }else {
                System.out.println("发送消息失败：对方不在线");
            }
        }catch (Exception e){
            System.out.println("发送消息失败：对方不在线");
            e.printStackTrace();
        }
    }

    /**
     * 对方不在线，先储存到中，待用户上线后再发送
     */





    // 根据 ID 查找所有符合条件的 WebSocketSession
    public static List<WebSocketSession> getSessionsById(Map<String, WebSocketSession> sessions, String targetId) {
        List<WebSocketSession> matchingSessions = new ArrayList<>();

        // 遍历所有的 WebSocketSession
        for (WebSocketSession session : sessions.values()) {
            // 获取 session 的属性 (Map<String, Object>)
            Object id = session.getAttributes().get("id");  // 获取 ID 属性

            // 如果属性存在且 ID 匹配目标 ID，添加到结果列表中
            if (id != null && id.equals(targetId)) {
                matchingSessions.add(session);
            }
        }
        return matchingSessions;  // 返回所有符合条件的 WebSocketSession
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("WebSocket连接已关闭，当前用户数：" + sessions.size());
    }


    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> paramMap = new HashMap<>();
        String[] pairs = query.split("&");  // 分割查询字符串成键值对
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");  // 分割每个键值对
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");  // 解码键
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");  // 解码值
                    paramMap.put(key, value);  // 存储键值对到 Map
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  // 如果编码错误，处理异常
                }
            }
        }
        return paramMap;  // 返回解析后的 Map
    }






    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        System.out.println("接收到消息：" + message);
        try {
            //获取接收到的二进制数据转byte
            ByteBuffer byteBuffer = message.getPayload();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);


            //判断socket会话是否关闭 关闭则不进行发送数据操作
            if (session.isOpen() && session instanceof StandardWebSocketSession) {
                StandardWebSocketSession standardSession = (StandardWebSocketSession) session;
                if (standardSession.isOpen() && standardSession.getRemoteAddress() != null) {

                    // 获取文档ID 给相应文档的在线用户推送消息
                    URI uri = session.getUri();
                    String query = uri.getQuery();
                    Map<String, String> params = parseQueryParams(query);
                    String doc = params.get("doc");

                    // 发送二进制消息给所有在线用户
                    getSessionsById(sessions, doc).forEach(wsSession -> {
                        try {
                            // 检查 WebSocket session 是否处于有效状态
                            if (wsSession.isOpen()) {
                                synchronized (wsSession) {
                                    wsSession.sendMessage(new BinaryMessage(bytes));
                                    logger.info("Successfully sent binary message to: " + wsSession.getId());
                                }
                            } else {
                                logger.warn("WebSocket session is closed or unavailable.");
                            }
                        } catch (IOException e) {
                            logger.error("Failed to send binary message", e);
                        }
                    });
                } else {
                    // 如果会话不可用或关闭，处理这个情况
                    logger.error("WebSocket session is closed or unavailable.");
                }
            } else {
                logger.warn("Session is not an instance of StandardWebSocketSession or session is closed.");
            }
        } catch (Exception e) {
            // 捕获所有异常
            logger.error("Unexpected error occurred while handling binary message:", e);
            // 关闭会话或其他处理措施
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException closeEx) {
                logger.error("Failed to close session after unexpected error: ", closeEx);
            }
        }
    }
}
