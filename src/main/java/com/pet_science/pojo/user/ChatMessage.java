package com.pet_science.pojo.user;

import lombok.Data;

@Data
public class ChatMessage {
    private String content;
    private String senderId;
    private String receiverId;
    private Long timestamp;
}
