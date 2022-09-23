package com.itheima.tanhua.vo.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVo implements Serializable {

    private Long id;
    private String nickname;
    private String avatar;
    private String tags;
    private String gender;
    private String age;
    private String education;
    private String city;
    private String birthday;
    private String profession;
    private String income;
    private Integer marriage;
}
