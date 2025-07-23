package com.dying.domain.request;

import lombok.Data;
import java.io.Serializable;

@Data
public class TeamChatMessageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long teamId;
    private String content;
} 