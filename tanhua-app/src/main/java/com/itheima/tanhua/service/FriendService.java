package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.autoconfig.template.HuanXinTemplate;
import com.itheima.tanhua.api.mongo.FriendServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.mongo.Friend;
import com.itheima.tanhua.utils.Constants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private FriendServiceApi friendServiceApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;



    /**
     * @description: 添加好友
     * @author: 黄伟兴
     * @date: 2022/9/27 15:05
     * @param: [map]
     * @return: void
     **/
    public void saveContacts(Long friendId) {
        //当前登录者id
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //3、建立环信好友关系 hx1
        Boolean flag = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + currentUserId, Constants.HX_USER_PREFIX + friendId);
        if (!flag){
            throw new ConsumerException("环信好友添加失败！");
        }

        friendServiceApi.saveContacts(currentUserId, friendId);
    }

    /**
     * @description: 根据id查询好友的id
     * @author: 黄伟兴
     * @date: 2022/9/27 15:55
     * @param: [currentUserId]
     * @return: java.util.List<java.lang.Long>
     **/
    public List<Friend> findFriend(Long currentUserId, Integer page, Integer pagesize) {
      return friendServiceApi.findFriend(currentUserId,page,pagesize);
    }

    /**
     * @description: 删除好友关系
     * @author: 黄伟兴
     * @date: 2022/9/29 21:02
     * @param: [friendId]
     * @return: void
     **/
    public void deleteContacts(Long friendId) {

        //当前登录者id
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //3、建立环信好友关系 hx1
        Boolean flag = huanXinTemplate.deleteContact(Constants.HX_USER_PREFIX + currentUserId, Constants.HX_USER_PREFIX + friendId);
        if (!flag){
            throw new ConsumerException("环信删除添加失败！");
        }

        friendServiceApi.deleteContacts(currentUserId, friendId);
    }
    /**
     * 添加好友
     *
     * @param friendId
     */
    public void contacts(Long friendId) {
        String uid = redisTemplate.opsForValue().get("AUTH_USER_ID");
        //1、将好友关系注册到环信
        Boolean aBoolean = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + friendId, Constants.HX_USER_PREFIX + uid);
        //2、如果注册成功，记录好友关系到mongodb
        if (!aBoolean) {
            throw new ConsumerException("注册到环信失败");
        }
        friendServiceApi.save(uid,friendId);

    }

    /**
     *获取所有好友的id
     * @param uid
     * @return
     */
    public List<Long> findFriendIds(String uid) {
        List <Friend> friends=friendServiceApi.findByUid(uid);
        List<Long> friendIds = CollUtil.getFieldValues(friends, "friendId",Long.class);
        return  friendIds;
    }

    /**
     * 删除好友关系
     * @param likeUserId
     */
    public void deleteFriend(Long likeUserId) {

        String uid = redisTemplate.opsForValue().get("AUTH_USER_ID");
        //1、将好友关系从环信删除
        Boolean aBoolean = huanXinTemplate.deleteContact(Constants.HX_USER_PREFIX + likeUserId.toString(), Constants.HX_USER_PREFIX + uid);
        //2、如果删除成功，移除mongodb好友关系
        if (!aBoolean) {
            throw new ConsumerException("从环信中删除好友失败");
        }
        friendServiceApi.remove(uid,likeUserId);
    }
}
