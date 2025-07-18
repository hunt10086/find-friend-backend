package com.dying.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/18 17:00
 */
@Data
public class BlogVo {
    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 文章
     */
    private String passage;

    /**
     * 发表用户Id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 文章类型
     */
    private String kind;

    /**
     * 文章获赞
     */
    private Integer praise;

    /**
     * 头像
     */
    private String avatarUrl;
}
