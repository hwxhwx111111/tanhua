package com.itheima.tanhua.api;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.mapper.UserInfoMapper;
import com.itheima.tanhua.pojo.db.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

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
        userInfoMapper.update(userInfo, new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, userInfo.getId()));
    }

    /**
     * @description: 根据用户id查询用户详细信息
     * @author: 黄伟兴
     * @date: 2022/9/24 19:40
     * @param: [userId]
     * @return: com.itheima.tanhua.pojo.db.UserInfo
     **/
    @Override
    public UserInfo findById(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        return userInfo;
    }

    /**
     * @description: 根据id查询用户
     * @author: 黄伟兴
     * @date: 2022/9/29 1:14
     * @param: [userIds, o]
     * @return: java.util.Map<java.lang.Long,com.itheima.tanhua.pojo.db.UserInfo>
     **/
    @Override
    public Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo userInfo) {

        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        //1.条件
        qw.in("id",userIds);
//        if(userInfo!=null){
//            qw.lt("age",userInfo.getAge());
//        }
//        if(userInfo!=null &&StringUtils.isNotEmpty(userInfo.getGender())){
//            qw.eq("gender",userInfo.getGender());
//        }
//        if(userInfo!=null&&!StringUtils.isEmpty(userInfo.getNickname())){
//            qw.like("nickname",userInfo.getNickname());
//        }


        List<UserInfo> userInfoList = userInfoMapper.selectList(qw);
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(userInfoList, "id");
        return map;
    }

    /**
     * @description: 联系人列表分页查询及条件查询
     * @author: 黄伟兴
     * @date: 2022/9/29 1:13
     * @param: [friendIds, page, pagesize, keyword]
     * @return: java.util.List<com.itheima.tanhua.pojo.db.UserInfo>
     **/
    @Override
    public List<UserInfo> findPageByIds(List<Long> friendIds, Integer page, Integer pagesize, String keyword) {

        Page<UserInfo> userInfoPage = userInfoMapper.selectPage(new Page<UserInfo>(page,pagesize),
                new LambdaQueryWrapper<UserInfo>().in(UserInfo::getId,friendIds)
                        .like(StrUtil.isNotBlank(keyword),UserInfo::getNickname,keyword));
        return userInfoPage.getRecords();
    }


}
