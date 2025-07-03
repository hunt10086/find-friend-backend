package com.dying.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable{
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别（0-未知 1-男 2-女）
     */
    private Integer gender;


    /**
     * 标签
     */
    private String tags;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0 - 未删除 1 - 删除
     */
    @TableLogic
    private Integer isdelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;


    /**
     * 个人简介
     */
    private String profile;
}