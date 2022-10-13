package com.itheima.tanhua.service;

import com.itheima.tanhua.api.db.NotificationsApi;
import com.itheima.tanhua.pojo.db.Notifications;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference
    private NotificationsApi notificationsApi;

    /**
     * 设置通知
     *
     * @param likeNotification
     * @param pinglunNotification
     * @param gonggaoNotification
     */
    public void notificationsSet(boolean likeNotification, boolean pinglunNotification, boolean gonggaoNotification) {
//获取当前用户的id
        String uid = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");

        Notifications notifications = notificationsApi.findByUid(uid);
        //3、判断,如果未查到通知数据新增,查到的话更新
        if (notifications == null) {
            //保存
            notifications = new Notifications();
            notifications.setUserId(Long.valueOf(uid));
            notifications.setPinglunNotification(pinglunNotification);
            notifications.setLikeNotification(likeNotification);
            notifications.setGonggaoNotification(gonggaoNotification);
            notificationsApi.save(notifications);
        } else {
            notifications.setPinglunNotification(pinglunNotification);
            notifications.setLikeNotification(likeNotification);
            notifications.setGonggaoNotification(gonggaoNotification);
            notificationsApi.update(notifications);
        }

    }
}
