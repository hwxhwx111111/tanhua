package com.itheima.tanhua.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.mapper.UserInfoMapper;
import com.itheima.tanhua.pojo.db.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService //发布到注册中心
public class UserInfoServiceImpl implements UserInfoServiceApi {

    @Autowired
    private UserInfoMapper userInfoMapper;


    /**
     * @description: 添加用户详细信息
     * @author: 黄伟兴
     * @date: 2022/9/20 9:45
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    /**
     * @description: 保存用户头像地址
     * @author: 黄伟兴
     * @date: 2022/9/20 11:35
     * @param: [userInfo]
     * @return: void
     **/
    @Override
    public void updateAvatar(UserInfo userInfo) {
        userInfoMapper.update(userInfo,new LambdaQueryWrapper<UserInfo>()
        .eq(UserInfo::getId,userInfo.getId()));
    }

    @Override
    public UserInfo findById(Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);

        return userInfo;
    }
}
