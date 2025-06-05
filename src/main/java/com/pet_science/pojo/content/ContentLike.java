package com.pet_science.pojo.content;

import lombok.Data;

@Data
public class ContentLike {
    private String like_id;
    private String user_id;
    private Content content;
    private String create_time;
    private boolean isLike;
}
