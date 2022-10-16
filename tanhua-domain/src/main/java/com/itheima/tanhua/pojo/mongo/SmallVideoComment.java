package com.itheima.tanhua.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 袁鹏
 * @date 2022-10-06-17:49
 */
@Data
@Document("smallvideo_comment")
public class SmallVideoComment {
    @Id
    private String id;
    private Long userId;    // 评论者id
    private String videoId;    // 被评论视频id
    private Integer commentType;    // 评论类型, 0: 点赞, 1: 评论
    private String content; // 内容
}
