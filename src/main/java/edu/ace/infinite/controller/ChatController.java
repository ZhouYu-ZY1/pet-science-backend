package edu.ace.infinite.controller;

import edu.ace.infinite.pojo.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller("chatController")
public class ChatController {
    @MessageMapping("/send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        // 处理并广播消息
        return chatMessage;
    }
    @MessageMapping("/chat")
    @SendToUser("/topic/private")
    public ChatMessage sendPrivateMessage(ChatMessage chatMessage) {
        // 发送私聊消息
        return chatMessage;
    }
}
