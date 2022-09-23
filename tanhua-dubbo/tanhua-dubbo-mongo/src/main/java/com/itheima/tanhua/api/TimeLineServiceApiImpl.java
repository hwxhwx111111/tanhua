package com.itheima.tanhua.api;


import cn.hutool.core.collection.CollUtil;
import com.itheima.tanhua.api.mongo.TimeLineServiceApi;
import com.itheima.tanhua.pojo.mongo.Friend;
import com.itheima.tanhua.pojo.mongo.MovementTimeLine;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class TimeLineServiceApiImpl implements TimeLineServiceApi {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @description: 保存动态时间线
     * @author: 黄伟兴
     * @date: 2022/9/24 23:33
     * @param: [uid, mid]
     * @return: void
     **/
    @Override
    public void saveTimeLine(Long uid, ObjectId mid) {

        //查询好友列表
        Query query = Query.query(Criteria.where("userId").is(uid));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        for (Friend friend : friends) {
            MovementTimeLine movementTimeLine = new MovementTimeLine();
            movementTimeLine.setMovementId(mid);
            movementTimeLine.setCreated(System.currentTimeMillis());
            movementTimeLine.setUserId(uid);
            movementTimeLine.setFriendId(friend.getFriendId());

            //保存时间线
            mongoTemplate.save(movementTimeLine);
        }

    }

    /**
     * @description: 根据userid查询时间线表中对应好友的 发布动态id
     * @author: 黄伟兴
     * @date: 2022/9/25 16:19
     * @param: [userId]
     * @return: java.util.List<java.lang.Object>
     **/
    @Override
    public List<Object> findMovementIds(Long userId) {
        //TODO 这里没懂
        Query query = Query.query(Criteria.where("friendId").is(userId));
        List<MovementTimeLine> movementTimeLines = mongoTemplate.find(query, MovementTimeLine.class);
        //返回movement  的ids
        return  CollUtil.getFieldValues(movementTimeLines, "movementId");
    }


}
