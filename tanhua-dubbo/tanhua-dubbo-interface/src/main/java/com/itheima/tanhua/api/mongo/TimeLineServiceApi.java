package com.itheima.tanhua.api.mongo;

import org.bson.types.ObjectId;

import java.util.List;

public interface TimeLineServiceApi {

    /**
     * @description: 保存动态时间线表
     * @author: 黄伟兴
     * @date: 2022/9/25 15:31
     * @param: [uid, mid]
     * @return: void
     **/
    void saveTimeLine(Long uid, ObjectId mid);

    /**
     * @description: 根据userid查询时间线表中对应好友的 发布动态id
     * @author: 黄伟兴
     * @date: 2022/9/25 16:19
     * @param: [userId]
     * @return: java.util.List<java.lang.Object>
     **/
    List<Object> findMovementIds(Long userId);
}
