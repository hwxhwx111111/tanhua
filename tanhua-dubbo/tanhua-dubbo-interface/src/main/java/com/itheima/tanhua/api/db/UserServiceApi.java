package com.itheima.tanhua.api.db;

import com.itheima.tanhua.pojo.db.User;

public interface UserServiceApi {

    //根据手机号查询用户
    User findByPhone(String phone);
    //02 登录---验证码校验
    void save(User user);

}
