package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.Friend;

import java.util.List;

public interface FriendServiceApi {

    /**
     * @description: 保存好友关系表
     * @author: 黄伟兴
     * @date: 2022/9/29 1:04
     * @param: [currentUserId, friendId]
     * @return: void
     **/
    void saveContacts(Long currentUserId, Long friendId);

    /**
     * @description: 查询好友的id
     * @author: 黄伟兴
     * @date: 2022/9/29 1:23
     * @param: [currentUserId, page, pagesize]
     * @return: java.util.List<java.lang.Long>
     **/
    List<Friend> findFriend(Long currentUserId, Integer page, Integer pagesize);

    void deleteContacts(Long currentUserId, Long friendId);
}
