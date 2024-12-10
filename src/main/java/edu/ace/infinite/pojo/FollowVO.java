package edu.ace.infinite.pojo;

import lombok.Data;
@Data
public class FollowVO {
        private Integer fromUserId;
        private String nickname;
        private Integer ToUserId;
        private Integer isMutual;//互关
}
