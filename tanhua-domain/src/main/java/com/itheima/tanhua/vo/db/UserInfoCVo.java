package com.itheima.tanhua.vo.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoCVo implements Serializable {
    private Integer id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;
    private String education;
    private Integer marriage;
    private Integer matchRate;
    private Boolean alreadyLove;
}
