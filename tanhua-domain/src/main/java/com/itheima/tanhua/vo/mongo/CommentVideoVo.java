package com.itheima.tanhua.vo.mongo;


import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.CommentVideo;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class CommentVideoVo {
    private String id;
    private String avatar;
    private String nickname;
    private String content;
    private String createDate;
    private Integer likeCount;
    private Integer hasLiked;

    public static CommentVideoVo init(UserInfo userInfo, CommentVideo commentVideo, boolean hasLiked){
        CommentVideoVo commentVo = new CommentVideoVo();
        commentVo.id = commentVideo.getId();
        commentVo.avatar = userInfo.getAvatar();
        commentVo.nickname = userInfo.getNickname();
        commentVo.content = commentVideo.getContent();
        commentVo.createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(commentVideo.getCreated()));
        commentVo.likeCount = commentVideo.getLikeCount();
        // todo 评论点赞的redisKey未知, 只能赋默认值
        commentVo.hasLiked = hasLiked ? 1 : 0;
        return commentVo;
    }
}
