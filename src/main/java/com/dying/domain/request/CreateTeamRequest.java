package com.dying.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author daylight
 * @Date 2025/7/10 10:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamRequest {
    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 0 - 无加密  1- 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
