package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Friend;
import com.itheima.tanhua.vo.db.UserInfoVo;
import com.itheima.tanhua.vo.mongo.ContactVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessagesService {

    @DubboReference
    private UserServiceApi userServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FriendService friendService;

    /**
     * @description: 使用环信id查询用户信息
     * @author: 黄伟兴
     * @date: 2022/9/29 1:06
     * @param: [huanxinId]
     * @return: com.itheima.tanhua.vo.db.UserInfoVo
     **/
    public UserInfoVo findUserInfoByHuanxinId(String huanxinId) {
        User user = userServiceApi.findByHuanxinId(huanxinId);
        UserInfo userInfo = userInfoServiceApi.findById(user.getId());
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,vo);
        if (userInfo.getAvatar()!=null){
            vo.setAge(String.valueOf(userInfo.getAge()));
        }

        return vo;
    }

    /**
     * @description: 联系人列表分页查询
     * @author: 黄伟兴
     * @date: 2022/9/27 15:26
     * @param: [page, pagesize, keyword]
     * @return: org.springframework.http.ResponseEntity
     **/
    public PageResult<ContactVo> findContacts(Integer page, Integer pagesize, String keyword) {
        //1.当前登录者id
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //2.在mongodb中根据id查询好友,并分页
        List<Friend> friends = friendService.findFriend(currentUserId,page,pagesize);
        if(CollUtil.isEmpty(friends)){
            return new PageResult<>();
        }
        List<Long> friendIdList = CollUtil.getFieldValues(friends, "friendId", Long.class);

        //3.调用接口查询好友的详情 ，添加查询条件
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(keyword);
        //朋友id-->朋友信息
        Map<Long, UserInfo> map = userInfoServiceApi.findByIds(friendIdList, userInfo);


        ArrayList<ContactVo> contactVoList = new ArrayList<>();
        //4.构造vo对象
        for (Long friendId : friendIdList) {
            UserInfo userInfo1 = map.get(friendId);
            if(userInfo1!=null){
                ContactVo contactVo = ContactVo.init(userInfo1);
                contactVoList.add(contactVo);
            }
        }
        return new PageResult<>(page,pagesize,0L,contactVoList);
    }

    /**
     * @description: 添加好友
     * @author: 黄伟兴
     * @date: 2022/9/27 15:05
     * @param: [map]
     * @return: void
     **/
    public void saveContacts(Long friendId) {
        friendService.saveContacts(friendId);
    }

    public void deleteContacts(Long friendId) {
        friendService.deleteContacts(friendId);
    }
}
