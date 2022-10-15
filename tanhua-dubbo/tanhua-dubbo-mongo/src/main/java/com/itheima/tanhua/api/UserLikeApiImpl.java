package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.UserLikeApi;

import com.itheima.tanhua.pojo.mongo.UserLike;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@DubboService
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 将喜欢信息保存到数据库
     *
     * @param uid
     * @param likeUserId
     * @param isLike
     * @return
     */
    @Override
    public Boolean saveOrUpdate(Long uid, Long likeUserId, boolean isLike) {
       //根据传递参数判断数据库存不存在喜欢数据,存在更新,不存在添加保存

        try {
            Query query = Query.query(Criteria.where("userId").is(uid).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            //2、如果不存在，保存
            if (userLike == null) {
                userLike = new UserLike();
                userLike.setUserId(uid);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            } else {

                Update update = Update.update("isLike", isLike).set("updated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean deleteOrUpdate(Long uid, Long likeUserId, boolean isLike) {
        //根据传递参数判断数据库存不存在喜欢数据,存在更新,不存在添加保存
        try {
            Query query = Query.query(Criteria.where("userId").is(uid).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            //2、如果存在，保存
            if (userLike != null) {
                Query query1 = Query.query(Criteria.where("userId").is(uid));
                mongoTemplate.remove(query1,UserLike.class);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

