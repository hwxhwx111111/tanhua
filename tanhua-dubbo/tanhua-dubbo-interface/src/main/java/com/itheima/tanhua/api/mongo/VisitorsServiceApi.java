package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.Visitors;

import java.util.List;

public interface VisitorsServiceApi {
    /*
     * @description: 保存访客数据
     * @author: 黄伟兴
     * @date: 2022/10/5 18:29
     * @param: [visitors]
     * @return: void
     **/
    void save(Visitors visitors);

    List<Visitors> queryMyVisitors(Long date, Long currentUserId);
}
