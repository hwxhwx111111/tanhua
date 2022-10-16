package com.itheima.tanhua.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author 袁鹏
 * @date 2022-09-23-9:11
 */
@Data
@Document("comment_video")
public class CommentVideo implements Serializable {
    @Id
    private String id;
    private String cid;      //评论id(对评论回复或点赞)
    private String videoId;    //视频id(对视频评论或点赞)
    private Integer commentType;   //评论类型，1-点赞，2-评论，3-喜欢
    private String content;        //评论内容
    private Long userId;           //评论人
    private Long videoUserId;    //被评论人ID
    private Long created; 		   //发表时间

    private Integer likeCount = 0; //当前评论的点赞数
}
