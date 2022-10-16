package com.itheima.tanhua.api;

import cn.hutool.core.collection.CollUtil;

import com.itheima.tanhua.api.mongo.PeachBlossomApi;
import com.itheima.tanhua.pojo.mongo.PeachBlossom;
import com.itheima.tanhua.pojo.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@DubboService
public class PeachBlossomApiImpl implements PeachBlossomApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(PeachBlossom peachBlossom) {
        mongoTemplate.save(peachBlossom);
    }

    @Override
    public PeachBlossom getOne(Long userId) {
        List<UserLike> userLikeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        List<Long> likeUserId = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("userId").nin(likeUserId)), Aggregation.sample(1));
        AggregationResults<PeachBlossom> aggregate = mongoTemplate.aggregate(aggregation, PeachBlossom.class, PeachBlossom.class);
        if(CollUtil.isNotEmpty(aggregate.getMappedResults())) {
            return aggregate.getMappedResults().get(0);
        }
        return null;
    }
}
