package com.itheima.tanhua.pojo.db;

import lombok.Data;


@Data
public class User extends BasePojo {

    private Long id;
    private String mobile;
    private String password;

    private String hxUser;
    private String hxPassword;

}
