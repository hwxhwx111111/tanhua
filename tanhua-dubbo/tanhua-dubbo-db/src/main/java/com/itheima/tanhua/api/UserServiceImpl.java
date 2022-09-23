package com.itheima.tanhua.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.mapper.UserMapper;
import com.itheima.tanhua.pojo.db.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService //发布到注册中心
public class UserServiceImpl implements UserServiceApi {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByPhone(String phone) {

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getMobile,phone));
        return user;
    }

    //02 保存用户基本信息
    @Override
    public void save(User user) {
        userMapper.insert(user);
    }
}
