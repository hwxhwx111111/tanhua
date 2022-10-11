package com.itheima.tanhua.vo.mongo;
import lombok.Data;

import java.io.Serializable;
@Data
public class VideoVoNew implements Serializable {
    private Long id;
    private String nickname;
    private Long userId;
    private Long createDate;
    private String videoUrl;
    private String picUrl;
    private Integer likeCount;
    private Integer commentCount;
}