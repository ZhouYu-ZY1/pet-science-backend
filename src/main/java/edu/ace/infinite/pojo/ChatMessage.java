package edu.ace.infinite.pojo;

import lombok.Data;

@Data
public class ChatMessage {
    private String content;
    private String senderId;
    private String receiverId;
    private Long timestamp;
}
