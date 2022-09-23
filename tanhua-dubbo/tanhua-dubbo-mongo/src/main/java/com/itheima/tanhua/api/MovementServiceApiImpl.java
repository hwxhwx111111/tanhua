package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.MovementServiceApi;
import com.itheima.tanhua.pojo.mongo.Movement;
import com.itheima.tanhua.utils.IdWorker;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
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
public class MovementServiceApiImpl implements MovementServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * @description: 发布动态
     * @author: 黄伟兴
     * @date: 2022/9/25 0:43
     * @param: [movement]
     * @return: org.bson.types.ObjectId
     **/
    @Override
    public ObjectId publish(Movement movement) {
        //TODO  有问题，暂时为空
        movement.setPid(idWorker.getNextId("movement"));
        Movement movement1 = mongoTemplate.save(movement);
        return movement1.getId();

    }

    /**
     * @description: 根据userId查询动态列表
     * @author: 黄伟兴
     * @date: 2022/9/24 9:54
     * @param: [userId, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    @Override
    public List<Movement> findMovementByUserId(Long userId, Integer page, Integer pagesize) {
        //PageRequest pageAble = PageRequest.of(page - 1, pagesize, Sort.by(Sort.Order.by("created")));

        Query query = Query.query(Criteria.where("userId").is(userId))
                .skip((page-1)*pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created"))); //按发布时间降序

        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return movements;
    }

    /**
     * @description: 查询好友动态
     * @author: 黄伟兴
     * @date: 2022/9/24 11:01
     * @param: [page, pagesize]
     * @return: com.itheima.tanhua.vo.mongo.PageResult<com.itheima.tanhua.vo.mongo.MovementsVo>
     **/
    @Override
    public List<Movement> findFriendMovements(Integer page, Integer pagesize, List<Object> movementIds) {
        //PageRequest pageAble = PageRequest.of(page - 1, pagesize, Sort.by(Sort.Order.by("created")));

        Query query = Query.query(Criteria.where("id").in(movementIds))
                .skip( (page-1)*pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.by("created")));
        return mongoTemplate.find(query, Movement.class);
    }

    /**
     * @description: 根据pid查询动态数据
     * @author: 黄伟兴
     * @date: 2022/9/25 20:01
     * @param: [pids]
     * @return: java.util.List<com.itheima.tanhua.vo.mongo.MovementsVo>
     **/
    @Override
    public List<Movement> findMovementByPids(List<Long> pids) {

        Query query = Query.query(Criteria.where("pid").in(pids));

       return mongoTemplate.find(query,Movement.class);
    }

    /**
     * @description: 调用API随机构造10条动态数据
     * @author: 黄伟兴
     * @date: 2022/9/25 20:06
     * @param: [pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    @Override
    public List<Movement> randomMovements(Integer pagesize) {
        //1.创建统计对象，设置统计参数
        TypedAggregation<Movement> aggregation = Aggregation.newAggregation(Movement.class,Aggregation.sample(pagesize));

        //2.调用mongoTemlate方法统计
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);

        //3.获取统计结果
        return results.getMappedResults();
    }

    @Override
    public Movement findMovementByMovementId(String movementId) {

        return mongoTemplate.findById(movementId,Movement.class);
    }
}
