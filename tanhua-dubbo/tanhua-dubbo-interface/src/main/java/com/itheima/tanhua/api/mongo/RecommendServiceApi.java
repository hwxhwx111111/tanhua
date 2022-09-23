package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.RecommendUser;

import java.util.List;

public interface RecommendServiceApi {


    //查询今日佳人
    RecommendUser todayBest(Long toUserId);


    List<RecommendUser> findByToUId(Long uid);
}
