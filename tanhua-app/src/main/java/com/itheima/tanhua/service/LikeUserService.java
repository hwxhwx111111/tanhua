package com.itheima.tanhua.service;

import com.itheima.tanhua.api.mongo.UserLikeApi;

import com.itheima.tanhua.exception.ConsumerException;

import com.itheima.tanhua.utils.Constants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeUserService {
    @DubboReference
    private UserLikeApi userLikeApi;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MessagesService messagesService;
    @Autowired
    private FriendService friendService;

    public void likeUser(Long likeUserId) {

        String uid = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");
        //将喜欢数据保存到mongodb
        Boolean save = userLikeApi.saveOrUpdate(Long.valueOf(uid), likeUserId, true);
        if (!save) {
            throw new ConsumerException("存入数据库失败");
        }
        //将数据保存到redis(写入喜欢的数据,删除不喜欢的数据)
        //删除不喜欢的数据
        stringRedisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + uid, likeUserId);
        //写入喜欢的数据
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY, likeUserId.toString());
        //判断是否为双向喜欢,是的话建立好友关系
        if (isLike(likeUserId, Long.valueOf(uid))) {
            friendService.contacts(likeUserId);
        }
    }


    public void notLikeUser(Long likeUserId) {

        String uid = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");
        //将喜欢数据保存到mongodb
        Boolean save = userLikeApi.saveOrUpdate(Long.valueOf(uid), likeUserId, false);
        if (!save) {
            throw new ConsumerException("存入数据库失败");
        }
        //将数据保存到redis(写入不喜欢的数据,删除喜欢的数据)
        //写入不喜欢的数据
        stringRedisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + uid, likeUserId.toString());
        //删除喜欢的数据
        stringRedisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY+ uid, likeUserId.toString());
        //判断是否为双向喜欢,是的话删除好友关系
        if (isLike(likeUserId, Long.valueOf(uid))) {
            friendService.deleteFriend(likeUserId);
        }
    }

    public Boolean isLike(Long userId, Long likeUserId) {
        //  (isMember方法)判断集合中是否存在数据
        return stringRedisTemplate.opsForSet().isMember(Constants.USER_LIKE_KEY + userId.toString(), likeUserId.toString());
    }

    /**
     * 喜欢粉丝
     *uid,粉丝id
     * @param uid
     */
    public void fansLike(String uid) {
        String userId = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");
        //将喜欢数据保存到mongodb
        Long fansId = Long.valueOf(uid);
        Boolean save = userLikeApi.saveOrUpdate(Long.valueOf(userId), fansId, true);
        if (!save) {
            throw new ConsumerException("存入数据库失败");
        }
        //将数据保存到redis(写入喜欢的数据,删除不喜欢的数据)
        //删除不喜欢的数据
        stringRedisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + userId, fansId.toString());
        //写入喜欢的数据
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY+ userId, fansId.toString());
        //判断是否为双向喜欢,是的话建立好友关系
        if (isLike(fansId, Long.valueOf(userId))) {
            friendService.contacts(fansId);
        }

    }
}

