package com.itheima.tanhua.api.mongo;



import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.vo.mongo.PageResult;

import java.util.List;


public interface RecommendUserApi {

    RecommendUser todayBest(Long userId);

    RecommendUser getRecommendUserById(Long toUserId, Long userId);

    PageResult<RecommendUser> getRecommendUserPage(Long userId, int page, int pageSize);

    List<RecommendUser> getCard(Long userId, int num);
}
