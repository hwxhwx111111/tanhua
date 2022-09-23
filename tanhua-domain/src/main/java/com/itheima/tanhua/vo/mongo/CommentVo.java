package com.itheima.tanhua.vo.mongo;


import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo implements Serializable {

    private String id; //评论id
    private String avatar; //头像
    private String nickname; //昵称


    private String content; //评论
    private String createDate; //评论时间

    private Integer likeCount; //点赞数
    private Integer hasLiked; //是否点赞（1是，0否）

    public static CommentVo init(UserInfo userInfo, Comment item) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(userInfo, commentVo);
        BeanUtils.copyProperties(item, commentVo);
        commentVo.setHasLiked(0);
        commentVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(item.getCreated())));
        commentVo.setId(item.getId().toHexString());
        return commentVo;
    }
}