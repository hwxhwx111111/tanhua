package com.itheima.tanhua.api;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.mapper.UserInfoMapper;
import com.itheima.tanhua.pojo.db.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DubboService //发布到注册中心
@Slf4j
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

    @Override
    public IPage<UserInfo> findPageUsers(Integer page, Integer pagesize) {

        IPage<UserInfo> iPage = new Page<>(page, pagesize);
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(UserInfo::getUpdated);
        userInfoMapper.selectPage(iPage, lqw);
        log.info("数据为{}",iPage.getRecords());
        return iPage;
    }

    @Override
    public Map<Long, UserInfo> getUserInfoMap(List<Long> userId, UserInfo condition) {
        List<UserInfo> userInfo = getUserInfo(userId, condition);
        Map<Long, UserInfo> map = new HashMap<>();
        userInfo.forEach(u -> map.put(u.getId(), u));
        return map;
    }
    @Override
    public List<UserInfo> getUserInfo(List<Long> userIds, UserInfo condition) {
        if(CollUtil.isEmpty(userIds)){
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfo::getId, userIds);
        if(condition != null) {
            queryWrapper.eq(StrUtil.isNotBlank(condition.getNickname()), UserInfo::getNickname, condition.getNickname())
                    .eq(StrUtil.isNotBlank(condition.getGender()), UserInfo::getGender, condition.getGender())
                    .eq(StrUtil.isNotBlank(condition.getEducation()), UserInfo::getEducation, condition.getEducation())
                    .like(StrUtil.isNotBlank(condition.getCity()), UserInfo::getCity, condition.getCity())
                    .eq(ObjectUtil.isNotNull(condition.getMarriage()), UserInfo::getMarriage, condition.getMarriage());
        }
        return userInfoMapper.selectList(queryWrapper).stream()
                .filter(u -> ObjectUtil.isNull(condition) || StrUtil.isBlank(Convert.toStr(condition.getAge())) || Integer.parseInt(Convert.toStr(u.getAge())) <= Integer.parseInt(Convert.toStr(condition.getAge())))
                .collect(Collectors.toList());
    }
    @Override
    public Map<Long, UserInfo> findbyIds(List<Long> userIds, UserInfo userInfo) {

        //查询
        QueryWrapper qw = new QueryWrapper();
        //1、用户id列表
        qw.in("id",userIds);
        //2、添加筛选条件
        if(userInfo != null) {
            if(ObjectUtil.isNotNull(userInfo.getAge())) {
                qw.lt("age",userInfo.getAge());
            }
            if(!StringUtils.isEmpty(userInfo.getGender())) {
                qw.eq("gender",userInfo.getGender());
            }
            if(!StringUtils.isEmpty(userInfo.getNickname())) {
                qw.like("nickname",userInfo.getNickname());
            }
        }
        List<UserInfo> list = userInfoMapper.selectList(qw);
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(list, "id");
        return map;
    }

    @Override
    public void updateUser(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }
}
