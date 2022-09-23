package com.itheima.tanhua.api.db;

import com.itheima.tanhua.pojo.db.User;

import java.util.List;

public interface UserServiceApi {

    //根据手机号查询用户
    User findByPhone(String phone);
    //02 登录---验证码校验
    void save(User user);

    List<User> findAll();

    User findById(String userId);

    void update(User user);

    User findByHuanxinId(String huanxinId);

    /**
     * @description: 查询环信用户信息
     * @author: 黄伟兴
     * @date: 2022/9/28 22:08
     * @param: []
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.db.HuanxinVo>
     **/
    User findHuanxinById(Long userId);
}
