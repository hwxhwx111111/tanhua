package com.itheima.tanhua.vo.mongo;

import lombok.Data;

import java.io.Serializable;
@Data
public class CommentVoNew implements Serializable {
    private String id;
    private String nickname;
    private Long userId;
    private String content;
    private Long createDate;

}