package com.itheima.tanhua.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.AnnouncementServiceApi;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.api.mongo.UserLikeServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.Announcement;
import com.itheima.tanhua.pojo.db.AnnouncementVo;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Comment;
import com.itheima.tanhua.pojo.mongo.Friend;
import com.itheima.tanhua.pojo.mongo.UserLike;
import com.itheima.tanhua.utils.RelativeDateFormat;
import com.itheima.tanhua.vo.db.UserInfoVo;
import com.itheima.tanhua.vo.mongo.ContactVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.UserLikeVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @DubboReference
    private AnnouncementServiceApi announcementServiceApi;

    @DubboReference
    private UserLikeServiceApi userLikeServiceApi;

    @Autowired
    private CommentService commentService;

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

    /**
     * 公告列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult announcements(Integer page, Integer pagesize) {
        List<Announcement> announcements = announcementServiceApi.findAnnouncements();
        List<AnnouncementVo> vos =new ArrayList<>();
        for (Announcement announcement : announcements) {
            AnnouncementVo vo = new AnnouncementVo();
            vo.setId(Convert.toStr(announcement.getId()));
            vo.setTitle(announcement.getTitle());
            vo.setDescription(announcement.getDescription());
            vo.setCreateDate(RelativeDateFormat.format(announcement.getCreated()));
            vos.add(vo);
        }

        return new PageResult(page,pagesize,Convert.toLong(vos.size()),vos);
    }

    /**
     * 喜欢列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult loves(Integer page, Integer pagesize) {
        //1.当前登录者id
        Long UserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        List<UserLike> userLikes= userLikeServiceApi.fendByIdLove(UserId);
        if (userLikes.size()==0){
            throw new ConsumerException("您还没有喜欢的");
        }
        List<Long>ids=new ArrayList<>();
        for (UserLike userLike : userLikes) {
            ids.add(userLike.getLikeUserId());
        }
        Map<Long, UserInfo> userInfoMap = userInfoServiceApi.findByIds(ids, null);
        List<UserInfo>userInfos=new ArrayList<>();
        for (Long id : ids) {
            UserInfo userInfo = userInfoMap.get(id);
            userInfos.add(userInfo);
        }
        if (userInfos==null){
            throw new ConsumerException("查询失败");
        }
        List<UserLikeVo> vos=new ArrayList<>();
        UserLikeVo vo=new UserLikeVo();

        for (UserInfo userInfo : userInfos) {
            vo.setId(Convert.toStr(userInfo.getId()));
            vo.setAvatar(userInfo.getAvatar());
            vo.setNickname(userInfo.getNickname());
            for (UserLike userLike : userLikes) {
                if (userLike.getLikeUserId().equals(userInfo.getId())){
                    vo.setCreateDate(RelativeDateFormat.format(new Date(userLike.getCreated())));
                }
            }
            vos.add(vo);
        }
        return new PageResult(page,pagesize,Convert.toLong(vos.size()),vos);
    }

    /**
     * 点赞列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult likes(Integer page, Integer pagesize) {
        //1.当前登录者id
        Long UserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        //根据用户id查询点赞数据
        List<Comment> commentList= commentService.findById(UserId);
        if (commentList.size()==0){
            throw new ConsumerException("您还没有点赞");
        }
        //获取被点赞的用户id
        List<Long>ids=new ArrayList<>();
        for (Comment comment : commentList) {
            ids.add(comment.getPublishUserId());
        }
        //根据被点赞的用户id查询用户详情
        Map<Long, UserInfo> userInfoMap = userInfoServiceApi.findByIds(ids, null);
        //将用户信息封装到集合
        List<UserInfo>userInfos=new ArrayList<>();
        for (Long id : ids) {
            UserInfo userInfo = userInfoMap.get(id);
            userInfos.add(userInfo);
        }
        //判断是否查询到数据
        if (userInfos==null){
            throw new ConsumerException("查询失败");
        }
        //定义一个集合去封装对象
        List<UserLikeVo> vos=new ArrayList<>();

        //取出数据存入集合
        for (UserInfo userInfo : userInfos) {
            UserLikeVo vo=new UserLikeVo();
            vo.setId(Convert.toStr(userInfo.getId()));
            vo.setAvatar(userInfo.getAvatar());
            vo.setNickname(userInfo.getNickname());
            for (Comment comment : commentList) {
                if (comment.getPublishUserId().equals(userInfo.getId())){
                    vo.setCreateDate(RelativeDateFormat.format(new Date(comment.getCreated())));
                }
            }

            vos.add(vo);
        }
        return new PageResult(page,pagesize,Convert.toLong(vos.size()),vos);
    }

    /**
     * 评论列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult comments(Integer page, Integer pagesize) {
        //1.当前登录者id
        Long UserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        //根据用户id查询评论数据
        List<Comment> commentList= commentService.findByIdLike(UserId);
        if (commentList.size()==0){
            throw new ConsumerException("您还没有评论");
        }
        //获取被点赞的用户id
        List<Long>ids=new ArrayList<>();
        for (Comment comment : commentList) {
            ids.add(comment.getPublishUserId());
        }
        //根据被点赞的用户id查询用户详情
        Map<Long, UserInfo> userInfoMap = userInfoServiceApi.findByIds(ids, null);
        //将用户信息封装到集合
        List<UserInfo>userInfos=new ArrayList<>();
        for (Long id : ids) {
            UserInfo userInfo = userInfoMap.get(id);
            userInfos.add(userInfo);
        }
        //判断是否查询到数据
        if (userInfos==null){
            throw new ConsumerException("查询失败");
        }
        //定义一个集合去封装对象
        List<UserLikeVo> vos=new ArrayList<>();

        //取出数据存入集合
        for (UserInfo userInfo : userInfos) {
            UserLikeVo vo=new UserLikeVo();
            vo.setId(Convert.toStr(userInfo.getId()));
            vo.setAvatar(userInfo.getAvatar());
            vo.setNickname(userInfo.getNickname());
            for (Comment comment : commentList) {
                if (comment.getPublishUserId().equals(userInfo.getId())){
                    vo.setCreateDate(RelativeDateFormat.format(new Date(comment.getCreated())));
                }
            }

            vos.add(vo);
        }
        return new PageResult(page,pagesize,Convert.toLong(vos.size()),vos);
    }
}
