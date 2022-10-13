package com.itheima.tanhua.api.db;

import com.itheima.tanhua.pojo.db.Notifications;


public interface NotificationsApi {
    Notifications findByUid(String uid);

    void save(Notifications notifications);

    void update(Notifications notifications);
}
