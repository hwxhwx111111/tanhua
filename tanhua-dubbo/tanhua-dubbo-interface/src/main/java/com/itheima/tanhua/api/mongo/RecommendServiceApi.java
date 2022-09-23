package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.RecommendUser;

import java.util.List;

public interface RecommendServiceApi {


    /**
     * @description:  //2.从mongodb中获取佳人信息
     * @author: 黄伟兴
     * @date: 2022/9/24 19:33
     * @param: [toUserId]
     * @return: com.itheima.tanhua.pojo.mongo.RecommendUser
     **/
    RecommendUser todayBest(Long toUserId);


    List<RecommendUser> findByToUId(Long uid,Integer page, Integer pagesize);

    Integer findById(Long currentId, Long userId);

    List<RecommendUser> queryCardList(Long userId, int count);
}
