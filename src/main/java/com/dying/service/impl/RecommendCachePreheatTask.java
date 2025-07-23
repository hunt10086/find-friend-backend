package com.dying.service.impl;

import com.dying.domain.User;
import com.dying.mapper.UserMapper;
import com.dying.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RecommendCachePreheatTask {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    // 每天凌晨2点预热所有用户的推荐缓存
    @Scheduled(cron = "0 0 2 * * ?")
    public void preheatRecommendCache() {
        List<User> allUsers = userMapper.selectList(null);
        for (User user : allUsers) {
            userService.backLike(user, 1);
            userService.backLike(user, 2);
        }
    }
} 