package com.dying.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/13 23:02
 */
@Data
public class UserVo {
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 标签
     */
    private String tags;


    /**
     * 个人简介
     */
    private String profile;
    /**
     *  经度 纬度
     */

    private Double distance;
}
