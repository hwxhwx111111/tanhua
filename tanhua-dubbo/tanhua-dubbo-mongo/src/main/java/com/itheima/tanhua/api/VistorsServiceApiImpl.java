package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.VisitorsServiceApi;
import com.itheima.tanhua.pojo.mongo.Visitors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class VistorsServiceApiImpl implements VisitorsServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @description: 保存访客数据  ，一天之内只能保存一次访客数据
     * @author: 黄伟兴
     * @date: 2022/10/5 18:31
     * @param: [visitors]
     * @return: void
     **/
    @Override
    public void save(Visitors visitors) {
        //1.查询访客数据
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId())
                .and("visitorUserId").is(visitors.getVisitorUserId())
                .and("visitDate").is(visitors.getVisitDate()));
        //2.不存在，保存
        if(!mongoTemplate.exists(query,Visitors.class)){
            mongoTemplate.save(visitors);
        }
    }

    /**
     * @description: 查询首页访客列表
     * @author: 黄伟兴
     * @date: 2022/10/5 19:16
     * @param: [date, currentUserId]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Visitors>
     **/
    @Override
    public List<Visitors> queryMyVisitors(Long date, Long currentUserId) {

        Criteria criteria = Criteria.where("userId").is(currentUserId);
        if(date!=null){
            criteria.and("date").gt(date);
        }
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));

        return mongoTemplate.find(query,Visitors.class);
    }
}
