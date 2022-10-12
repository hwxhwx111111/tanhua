package com.itheima.tanhua.api.db;

import com.itheima.tanhua.pojo.db.Announcement;
import com.itheima.tanhua.vo.mongo.PageResult;

import java.util.List;

public interface AnnouncementServiceApi {

    List<Announcement> findAnnouncements();
}
