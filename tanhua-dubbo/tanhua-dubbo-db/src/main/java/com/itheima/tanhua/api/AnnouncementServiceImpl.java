package com.itheima.tanhua.api;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.tanhua.api.db.AnnouncementServiceApi;
import com.itheima.tanhua.mapper.AnnouncementMapper;
import com.itheima.tanhua.pojo.db.Announcement;
import com.itheima.tanhua.pojo.db.AnnouncementVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class AnnouncementServiceImpl implements AnnouncementServiceApi {
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> findAnnouncements() {
        List<Announcement> announcementList = announcementMapper.selectList(null);
        return announcementList;
    }


}
