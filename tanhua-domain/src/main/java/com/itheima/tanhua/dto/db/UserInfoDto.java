package com.itheima.tanhua.dto.db;

import lombok.Data;

import java.io.Serializable;


//封装接受完善个人信息的前端传入参数的类
@Data
public class UserInfoDto implements Serializable {

    private String nickname; //昵称
    private String gender; //性别
    private String birthday; //生日
    private String city; //城市

    private String head; //用户头像


}