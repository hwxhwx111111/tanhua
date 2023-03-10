package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.UserLikeServiceApi;
import com.itheima.tanhua.pojo.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@DubboService
public class UserLikeServiceApiImpl implements UserLikeServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @description: 喜欢或者不喜欢
     * @author: 黄伟兴
     * @date: 2022/9/29 21:10
     * @param: [userId, likeUserId, b]
     * @return: java.lang.Boolean
     **/
    @Override
    public Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {

        try {
            //1、查询数据
            Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            //2、如果不存在，保存
            if (userLike == null) {
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            } else {
                //3、更新
                Update update = Update.update("isLike", isLike)
                        .set("updated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断userlike是否有数据
     *
     * @param userId
     * @param uid
     * @return
     */
    @Override
    public boolean isLike(String uid, Long userId) {
        try {
            Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(uid));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            if (userLike == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<UserLike> fendByIdLove(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        return userLikeList;
    }

    @Override
    public List<UserLike> fendByIdLike(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        return userLikeList;
    }

    //查询喜欢
    @Override
    public List<UserLike> selectLove(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        return userLikeList;
    }

    //查询粉丝
    @Override
    public List<UserLike> selectFan(Long userId) {
        Query query = Query.query(Criteria.where("likeUserId").is(userId));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        return userLikeList;
    }

    //查询相互喜欢
    @Override
    public List<UserLike> selectEachLove(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        List<UserLike> userLikes = new ArrayList<>();
        for (UserLike userLike : userLikeList) {
            Query query1 = Query.query(Criteria.where("userId").is(userLike.getLikeUserId()).and("likeUserId").is(userId));
            UserLike one = mongoTemplate.findOne(query1, UserLike.class);
            if (one != null) {
                userLikes.add(one);
            }
        }
        return userLikes;
    }

    //查询是否是粉丝
    @Override
    public Boolean findFs(Long userId, Long fanUser) {
        Query query = Query.query(Criteria.where("userId").is(fanUser).and("likeUserId").is(userId));
        UserLike one = mongoTemplate.findOne(query, UserLike.class);
        if (one != null) {
            return true;
        }
        return false;
    }
}
