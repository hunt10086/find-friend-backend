package com.dying.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class TeamChatMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long teamId;
    private Long userId;
    private String userName;
    private String content;
    private Date createTime;
    private String avatarUrl;
} 