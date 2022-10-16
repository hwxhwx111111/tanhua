package com.itheima.tanhua.api;

import cn.hutool.core.collection.CollUtil;

import com.itheima.tanhua.api.mongo.RecommendUserApi;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.pojo.mongo.UserLike;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser todayBest(Long userId) {
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public RecommendUser getRecommendUserById(Long toUserId, Long userId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId).and("userId").is(userId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageResult<RecommendUser> getRecommendUserPage(Long userId, int page, int pageSize) {
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, "recommend_user ");
        query.with(Sort.by(Order.desc("score"))).skip(1 + (page - 1) * pageSize).limit(pageSize);
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        PageResult<RecommendUser> recommendUserPageResult = new PageResult<>();
        recommendUserPageResult.setItems(recommendUsers);
        recommendUserPageResult.setPages(Double.valueOf(Math.ceil(count * 1.0 / pageSize)).longValue());
        recommendUserPageResult.setPagesize(pageSize);
        recommendUserPageResult.setCounts(count);
        recommendUserPageResult.setPage( page);
        return recommendUserPageResult;
    }

    @Override
    public List<RecommendUser> getCard(Long userId, int num) {
        Criteria criteria = Criteria.where("userId").is(userId);
        List<UserLike> userLikes = mongoTemplate.find(Query.query(criteria), UserLike.class);
        List<Long> likeUserId = CollUtil.getFieldValues(userLikes, "likeUserId", Long.class);

        Criteria nin = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserId);
        return mongoTemplate.find(Query.query(nin).limit(num), RecommendUser.class);
    }

}
