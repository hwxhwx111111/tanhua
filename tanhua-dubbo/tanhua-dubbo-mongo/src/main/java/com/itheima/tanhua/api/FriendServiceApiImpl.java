package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.FriendServiceApi;
import com.itheima.tanhua.pojo.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class FriendServiceApiImpl implements FriendServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * @description: 添加好友
     * @author: 黄伟兴
     * @date: 2022/9/27 14:59
     * @param: [currentUserId, friendId]
     * @return: void
     **/
    @Override
    public void saveContacts(Long currentUserId, Long friendId) {
        //添加2条记录
        Friend friend1 = new Friend();
        friend1.setCreated(System.currentTimeMillis());
        friend1.setUserId(currentUserId);
        friend1.setFriendId(friendId);
        mongoTemplate.save(friend1);

        Friend friend2 = new Friend();
        friend2.setCreated(System.currentTimeMillis());
        friend2.setUserId(friendId);
        friend2.setFriendId(currentUserId);
        mongoTemplate.save(friend2);
    }

    /**
     * @description: 删除好友关系
     * @author: 黄伟兴
     * @date: 2022/9/29 21:06
     * @param: [currentUserId, friendId]
     * @return: void
     **/
    @Override
    public void deleteContacts(Long currentUserId, Long friendId) {

        Query query1 = Query.query(Criteria.where("userId").is(currentUserId).and("friendId").is(friendId));
        Query query2 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(currentUserId));

        mongoTemplate.remove(query1,Friend.class);
        mongoTemplate.remove(query2,Friend.class);
    }

    /**
     * @description: 根据id查询好友并分页
     * @author: 黄伟兴
     * @date: 2022/9/29 1:42
     * @param: [currentUserId, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Friend>
     **/
    @Override
    public List<Friend> findFriend(Long currentUserId, Integer page, Integer pagesize) {
        Query query = Query.query(Criteria.where("userId").is(currentUserId)).skip((page-1)*pagesize).limit(pagesize);
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        return friends;
    }
    @Override
    public void save(String uid, Long friendId) {
        //保存当前用户的好友信息
        Friend friend = new Friend();
        friend.setFriendId(friendId);
        friend.setCreated(System.currentTimeMillis());
        friend.setUserId(Long.valueOf(uid));
        //save有则更新,无则添加,不需要判断
        mongoTemplate.save(friend);
        //环信添加成功后需要双向添加到数据库
        Friend friend1 = new Friend();
        friend1.setFriendId(Long.valueOf(uid));
        friend1.setCreated(System.currentTimeMillis());
        friend1.setUserId(friendId);
        mongoTemplate.save(friend1);
    }

    @Override
    public List<Friend> findByUid(String uid) {
        List<Friend> friends = mongoTemplate.find(Query.query(Criteria.where("userId").is(Long.valueOf(uid))), Friend.class);
        return friends;
    }

    @Override
    public void remove(String uid, Long likeUserId) {
        //双向删除
        Query query = Query.query(Criteria.where("userId").is(Long.valueOf(uid)).and("friendId").is(likeUserId));
        mongoTemplate.remove(query, Friend.class);

        Query query1 = Query.query(Criteria.where("friendId").is(Long.valueOf(uid)).and("userId").is(likeUserId));
        mongoTemplate.remove(query1, Friend.class);
    }
}
