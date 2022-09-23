package com.itheima.tanhua.service;

import com.itheima.tanhua.api.mongo.TimeLineServiceApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeLineService {

    @DubboReference
    private TimeLineServiceApi timeLineServiceApi;

    @Async
    public void saveTimeLine(Long uid, ObjectId mid){
        //查询好友列表//保存时间线
        timeLineServiceApi.saveTimeLine(uid,mid);
    }

    /**
     * @description: 根据userid查询时间线表中对应好友的 发布动态id
     * @author: 黄伟兴
     * @date: 2022/9/25 16:19
     * @param: [userId]
     * @return: java.util.List<java.lang.Object>
     **/
    public List<Object> findMovementIds(Long userId) {
      return   timeLineServiceApi.findMovementIds(userId);
    }
}
