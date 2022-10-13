package com.itheima.tanhua.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.api.db.NotificationsApi;
import com.itheima.tanhua.api.db.QuestionServiceApi;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.db.UserServiceApi;

import com.itheima.tanhua.pojo.db.Notifications;
import com.itheima.tanhua.vo.db.SettingsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference
    private QuestionServiceApi questionServiceApi;
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @DubboReference
    private NotificationsApi notificationsApi;
    @DubboReference
    private UserServiceApi userServiceApi;
    public SettingsVo settings() {
        //获取当前用户的id
        String uid = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");
        //查询当前用户设置的陌生人问题
        String strangerQuestions = questionServiceApi.findStrangerQuestions(Long.valueOf(uid));
        //获取当前用户的手机号
        String mobile = userServiceApi.findByUserId(uid).getMobile();
        //查询对应的通知设置
        Notifications notifications = notificationsApi.findByUid(uid);
        //封装vo对象
        SettingsVo settingsVo = new SettingsVo();
        if (StrUtil.isNotBlank(strangerQuestions)){
            settingsVo.setStrangerQuestion(strangerQuestions);
        }else{
            settingsVo.setStrangerQuestion("你喜欢Java吗?");
        }
        settingsVo.setPhone(mobile);
        if(ObjectUtil.isNotNull(notifications)){
            settingsVo.setLikeNotification(notifications.getLikeNotification());
            settingsVo.setPinglunNotification(notifications.getPinglunNotification());
            settingsVo.setGonggaoNotification(notifications.getGonggaoNotification());
        }
        return settingsVo;
    }
}
