package com.itheima.tanhua.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.tanhua.api.db.NotificationsApi;
import com.itheima.tanhua.mapper.NotificationsMapper;
import com.itheima.tanhua.pojo.db.Notifications;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DubboService
@Transactional
public class NotificationsApiImpl implements NotificationsApi {

    @Autowired
    private NotificationsMapper notificationsMapper;
    @Override
    public Notifications findByUid(String uid) {

        QueryWrapper<Notifications> qw = new QueryWrapper<>();
        qw.eq("user_id",Long.valueOf(uid));
        Notifications notifications = notificationsMapper.selectOne(qw);
        return notifications;
    }
    @Override
    public void save(Notifications notifications) {
        notificationsMapper.insert(notifications);
    }
    @Override
    public void update(Notifications notifications) {
        notificationsMapper.updateById(notifications);
    }
}
