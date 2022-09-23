package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class RecommendServiceApiImpl implements RecommendServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public RecommendUser todayBest(Long toUserId) {
        //1.查询条件
        Query query = Query.query(Criteria.where("toUserId").is(toUserId))
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(1);

        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        return recommendUser;
    }

    /**
     * @description: 查询佳人列表
     * @author: 黄伟兴
     * @date: 2022/9/23 1:56
     * @param: [uid]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.RecommendUser>
     **/
    @Override
    public List<RecommendUser> findByToUId(Long uid) {

        //1.查询条件
        Query query = Query.query(Criteria.where("toUserId").is(uid))
                .with(Sort.by(Sort.Order.desc("score")));

        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        return list;
    }
}
