package com.pet_science.pojo.user;

import lombok.Data;

@Data
public class FollowVO {
        private Integer fromUserId;
        private String nickname;
        private Integer ToUserId;
        private Integer isMutual;//互关
}
