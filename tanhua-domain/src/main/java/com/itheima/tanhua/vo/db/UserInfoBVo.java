package com.itheima.tanhua.vo.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoBVo implements Serializable {
    private Integer id;
    private String avatar;
    private String nickname;
    private String birthday;
    private String age;
    private String gender;
    private String city;
    private String education;
    private String income;
    private String profession;
    private Integer marriage;
}
