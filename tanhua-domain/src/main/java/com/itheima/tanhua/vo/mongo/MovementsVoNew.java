package com.itheima.tanhua.vo.mongo;


import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Movement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementsVoNew implements Serializable {

    private String id; //动态id
    private String nickname; //昵称
    private Long userId; //用户id
    private String avatar; //头像
    private String createDate; //发布时间 如: 10分钟前
    private String textContent; //文字动态
    private String[] imageContent; //图片动态
    private Integer state = 0;//状态 0：未审（默认），1：通过，2：驳回
    private Integer commentCount = 0; //评论数
    private Integer likeCount = 0; //点赞数

    //封装数据
    public static MovementsVoNew init(Movement movement, UserInfo userInfo) {

        MovementsVoNew vo = new MovementsVoNew();
        vo.setId(movement.getId().toHexString());
        vo.setNickname(userInfo.getNickname());
        vo.setUserId(movement.getUserId());
        vo.setAvatar(userInfo.getAvatar());
        vo.setCreateDate(movement.getCreated().toString());
        vo.setTextContent(movement.getTextContent());
        vo.setImageContent(movement.getMedias().toArray(new String[0]));
        vo.setState(movement.getState());
        vo.setCommentCount(movement.getCommentCount());
        vo.setLikeCount(movement.getLikeCount());
        return vo;
    }
}