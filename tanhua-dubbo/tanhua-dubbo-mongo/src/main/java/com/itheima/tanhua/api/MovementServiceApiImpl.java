package com.itheima.tanhua.api;

import cn.hutool.core.collection.CollUtil;
import com.itheima.tanhua.api.mongo.MovementServiceApi;
import com.itheima.tanhua.pojo.mongo.Movement;
import com.itheima.tanhua.pojo.mongo.UserLike;
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

import java.util.ArrayList;
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
                .skip((page - 1) * pagesize)
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
                .skip((page - 1) * pagesize)
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

        return mongoTemplate.find(query, Movement.class);
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
        TypedAggregation<Movement> aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(pagesize));

        //2.调用mongoTemlate方法统计
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);

        //3.获取统计结果
        return results.getMappedResults();
    }

    /**
     * 根据动态id查询
     * @param movementId
     * @return
     */
    @Override
    public Movement findMovementByMovementId(String movementId) {
        Movement movement = mongoTemplate.findById(movementId, Movement.class);
        return movement;
    }

    @Override
    public void save(UserLike userLike) {
        mongoTemplate.save(userLike);

    }

    /**
     * @description: 后台-根据用户id和动态状态分页查询动态
     * @author: 黄伟兴
     * @date: 2022/10/11 16:45
     * @param: [uid, state, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    @Override
    public List<Movement> findMovementByIdAndState(Long uid, Integer state, Integer page, Integer pagesize) {

        Criteria criteria=Criteria.where("userId").is(uid).and("state").is(state);

        Query query = Query.query(criteria)
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created"))); //按发布时间降序

        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return movements;
    }

    @Override
    public List<Movement> findMovementByIdAndState(Long uid, Integer state, Integer page, Integer pagesize, String sortProp, String sortOrder) {
        Criteria criteria=Criteria.where("userId").is(uid).and("state").is(state);


        Query query =new Query();
        if("descending".equals(sortOrder)){
           query = Query.query(criteria)
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.desc(sortProp))); //按发布时间降序
        }else{
            query = Query.query(criteria)
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.asc(sortProp))); //按发布时间降序
        }


        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return movements;
    }

    /**
     * @description: 动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:25
     * @param: [movementId]
     * @return: java.lang.Boolean
     **/
    @Override
    public Boolean setMovementTop(String movementId) {

        //TODO 待解决


        return true;
    }

    @Override
    public Boolean divestMovementTop(String movementId) {
        //TODO 待解决


        return true;
    }

    /**
     * @description: 动态通过
     * @author: 黄伟兴
     * @date: 2022/10/11 17:48
     * @param: [movementId]
     * @return: com.itheima.tanhua.vo.mongo.MovementsVoNew
     **/
    @Override
    public Boolean approveMovement(String[] movementIds) {

        for (String movementId : movementIds) {
            Movement movement = mongoTemplate.findById(movementId, Movement.class);
            movement.setState(1);
            Movement save = mongoTemplate.save(movement);
            if (save == null) {
                return false;
            }

        }
        return true;
    }

    /**
     * @description: 动态拒绝
     * @author: 黄伟兴
     * @date: 2022/10/11 18:00
     * @param: [movementIds]
     * @return: java.lang.Boolean
     **/
    @Override
    public Boolean rejectMovement(String[] movementIds) {

        for (String movementId : movementIds) {
            Movement movement = mongoTemplate.findById(movementId, Movement.class);
            movement.setState(2);
            Movement save = mongoTemplate.save(movement);
            if (save == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Long findAll(Long id,Integer state) {
        long count = mongoTemplate.count(new Query(Criteria.where("_id").is(id).and("state").is(state)), Movement.class);

        return count;
    }

    @Override
    public Long findAll() {
        long count = mongoTemplate.count(new Query(Criteria.where("state").in(0,1,2)), Movement.class);
        return count;
    }

    @Override
    public Long findAll( Integer state) {
        long count = mongoTemplate.count(new Query(Criteria.where("state").is(state)), Movement.class);

        return count;
    }

    @Override
    public ArrayList<Long> findUserIds() {

        List<Movement> movementList = mongoTemplate.findAll(Movement.class);
        List<Long> userIds = CollUtil.getFieldValues(movementList, "userId", Long.class);

        return (ArrayList<Long>) userIds;
    }

    @Override
    public List<Movement> findMovementByIdAndState1(ArrayList<Long> ids, Integer state, Integer page, Integer pagesize) {

        Criteria criteria=Criteria.where("userId").in(ids).and("state").is(state);

        Query query = Query.query(criteria)
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created"))); //按发布时间降序

        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return movements;
    }

    @Override
    public List<Movement> findMovementByIdAndState1(ArrayList<Long> ids, Integer state, Integer page, Integer pagesize, String sortProp, String sortOrder) {
        Criteria criteria=Criteria.where("userId").in(ids).and("state").is(state);
        Query query= new Query();

        if("descending".equals(sortOrder)){
             query = Query.query(criteria)
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.desc(sortProp))); //按发布时间降序
        }else{
             query = Query.query(criteria)
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.asc(sortProp))); //按发布时间降序
        }

        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return movements;
    }

    @Override
    public List<Movement> findMovementByIdAndState1(ArrayList<Long> ids, Integer page, Integer pagesize, String sortProp, String sortOrder) {
        Criteria criteria=Criteria.where("userId").in(ids);
        Query query= new Query();

        if("descending".equals(sortOrder)){
            query = Query.query(criteria)
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.desc(sortProp))); //按发布时间降序
        }else{
            query = Query.query(criteria)
                    .skip((page - 1) * pagesize)
                    .limit(pagesize)
                    .with(Sort.by(Sort.Order.asc(sortProp))); //按发布时间降序
        }

        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return movements;
    }
}
