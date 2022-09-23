package com.itheima.tanhua.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.mapper.UserMapper;
import com.itheima.tanhua.pojo.db.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService //发布到注册中心
public class UserServiceImpl implements UserServiceApi {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User findByPhone(String phone) {

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getMobile, phone));
        return user;
    }

    //02 保存用户基本信息
    @Override
    public void save(User user) {
        userMapper.insert(user);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public User findById(String userId) {
        User user = userMapper.selectById(userId);
        return user;
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public User findByHuanxinId(String huanxinId) {

        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("hx_user",huanxinId);
        User user = userMapper.selectOne(qw);
        return user;
    }

    @Override
    public User findHuanxinById(Long userId) {
        User user = userMapper.selectById(userId);
        if(user.getHxUser()==null||user.getHxPassword()==null){
            throw new ConsumerException("环信用户不存在或密码错误");
        }
        return user;
    }
}
