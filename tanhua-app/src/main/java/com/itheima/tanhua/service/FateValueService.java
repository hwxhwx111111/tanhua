package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.api.mongo.UserLikeServiceApi;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.vo.mongo.ApifoxModelVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class FateValueService {

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @DubboReference
    private RecommendServiceApi recommendServiceApi;
    @DubboReference
    private UserLikeServiceApi userLikeServiceApi;

    /**
     * 缘分值
     * @param id
     * @return
     */
    public ApifoxModelVo FateValue(String id) {
        ApifoxModelVo vo=new ApifoxModelVo();
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        //根据用户id查询用户信息
        UserInfo myUserInfo = userInfoServiceApi.findById(Convert.toLong(userId));
        UserInfo userInfo = userInfoServiceApi.findById(Convert.toLong(id));
        //封装实体类
        String myAvatar=myUserInfo.getAvatar();
        String tags = userInfo.getTags();
        //将标签进行分割
        String[] split = tags.split(",");
        String friendAvatar = userInfo.getAvatar();
        String toUserId=id;
        Integer fateValue=recommendServiceApi.fateValue(userId,toUserId);
        vo.setFateValue(fateValue);
        vo.setTags(Arrays.asList(split));
        vo.setFriendAvatar(friendAvatar);
        vo.setMyAvatar(myAvatar);
        return vo;
    }

    public boolean alreadyLove(String uid) {
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        boolean flag= userLikeServiceApi.isLike(uid,userId);
        return flag;
    }
}
