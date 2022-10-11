package com.itheima.tanhua.api;

import cn.hutool.core.collection.CollUtil;
import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.pojo.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class RecommendServiceApiImpl implements RecommendServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @description: //2.从mongodb中获取佳人信息
     * @author: 黄伟兴
     * @date: 2022/9/24 19:38
     * @param: [toUserId]
     * @return: com.itheima.tanhua.pojo.mongo.RecommendUser
     **/
    @Override
    public RecommendUser todayBest(Long toUserId) {
        //1.查询条件
        Query query = Query.query(Criteria.where("toUserId").is(toUserId))
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(1);

        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    /**
     * @description: 分页查询佳人列表
     * @author: 黄伟兴
     * @date: 2022/9/23 1:56
     * @param: [uid]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.RecommendUser>
     **/
    @Override
    public List<RecommendUser> findByToUId(Long uid, Integer page, Integer pagesize) {

        //1.查询条件
        Query query = Query.query(Criteria.where("toUserId").is(uid))
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("score")));

        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        return list;
    }

    /**
     * @description: 根据id查询佳人详情
     * @author: 黄伟兴
     * @date: 2022/9/29 2:14
     * @param: [currentId, userId]
     * @return: java.lang.Integer
     **/
    @Override
    public RecommendUser findById(Long currentId, Long userId) {
        Query query = Query.query(Criteria.where("toUserId").is(currentId).and("userId").is(userId));
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        return recommendUser;
    }

    /**
     * @description: 查询探花列表，查询时需要排除喜欢和不喜欢的用户
     * @author: 黄伟兴
     * @date: 2022/9/29 20:48
     * @param: [userId, count]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.RecommendUser>
     **/
    @Override
    public List<RecommendUser> queryCardList(Long userId, int count) {

        //1、查询喜欢不喜欢的用户ID
        List<UserLike> likeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        List<Long> likeUserIdS = CollUtil.getFieldValues(likeList, "likeUserId", Long.class);

        //2、构造查询推荐用户的条件
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserIdS);

        //3、使用统计函数，随机获取推荐的用户列表
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(
                RecommendUser.class,
                Aggregation.match(criteria),//指定查询条件
                Aggregation.sample(count)
        );

        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(newAggregation, RecommendUser.class);

        //4、构造返回
        return results.getMappedResults();
    }
}
