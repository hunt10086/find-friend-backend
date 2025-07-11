package com.dying.domain.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/9 15:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {

    private Long id;
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
     * 创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     * 密码
     */
    private String password;

    /**
     *  队伍头像
     */
    private String icon;



}
