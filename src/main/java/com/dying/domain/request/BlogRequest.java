package com.dying.domain.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/17 16:00
 */
@Data
public class BlogRequest {
    /**
     * 标题
     */
    private String title;
    /**
     * 文章
     */
    private String passage;
    /**
     * 文章类型
     */
    private String kind;
}
