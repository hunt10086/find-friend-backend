package com.dying.service;

import com.dying.domain.TeamChatMessage;
import com.dying.domain.vo.TeamChatMessageVo;

import java.util.List;

public interface TeamChatMessageService {
    boolean sendTeamChatMessage(Long userId, Long teamId, String content);
    List<TeamChatMessageVo> getTeamChatMessages(Long teamId);
} 