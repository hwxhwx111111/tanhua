package com.itheima.tanhua.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.itheima.autoconfig.template.HuanXinTemplate;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.UserLikeServiceApi;
import com.itheima.tanhua.api.mongo.UserLocationServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.mongo.NearUserVo;
import com.itheima.tanhua.vo.mongo.TodayBestVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TanhuaService {

    @Autowired
    private RecommendService recommendService;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserLikeServiceApi userLikeServiceApi;

    @Autowired
    private MessagesService messagesService;

    @DubboReference
    private UserLocationServiceApi userLocationServiceApi;

    //指定默认数据
    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;


    /**
     * @description: 探花-左滑右滑
     * @author: 黄伟兴
     * @date: 2022/9/29 20:31
     * @param: []
     * @return: org.springframework.http.ResponseEntity
     **/
    public List<TodayBestVo> queryCardsList() {
        //1.调用推荐api查询数据列表（排除喜欢/不喜欢，数量限制）
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        List<RecommendUser> recommendUserList = recommendService.queryCardList(currentUserId, 10);

        //2.判断数据是否存在，如果不存在，构造默认数据
        if (CollUtil.isEmpty(recommendUserList)) {
            recommendUserList = new ArrayList<>();
            String[] userIds = recommendUser.split(",");
            for (String userId : userIds) {
                RecommendUser rUser = new RecommendUser();
                rUser.setUserId(Convert.toLong(userId));
                rUser.setToUserId(currentUserId);
                rUser.setScore(RandomUtil.randomDouble(60, 90));
                recommendUserList.add(rUser);
            }
        }
        //3.构造vo
        List<Long> ids = CollUtil.getFieldValues(recommendUserList, "userId", Long.class);
        Map<Long, UserInfo> infoMap = userInfoServiceApi.findByIds(ids, null);

        List<TodayBestVo> vos = new ArrayList<>();
        for (RecommendUser user : recommendUserList) {
            UserInfo userInfo = infoMap.get(user.getUserId());
            if (userInfo != null) {
                TodayBestVo vo = TodayBestVo.init(userInfo, user);
                vos.add(vo);
            }
        }
        return vos;
    }

    /*
     * @description: 探花喜欢
     * @author: 黄伟兴
     * @date: 2022/10/5 16:55
     * @param: [likeUserId]
     * @return: void
     **/
    public void likeUser(Long likeUserId) {
        //当前登录者的id
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1.调用API，保存喜欢数据（保存到mongodb中）
        Boolean save = userLikeServiceApi.saveOrUpdate(currentUserId, likeUserId, true);
        if (!save) {
            throw new ConsumerException("喜欢失败");
        }

        //2.操作redis，写入喜欢的数据，删除不喜欢的数据(喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + currentUserId, likeUserId.toString());
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + currentUserId, likeUserId.toString());

        //3、判断是否双向喜欢
        if (isLike(likeUserId, currentUserId)) {
            //4、添加好友
            messagesService.saveContacts(likeUserId);
        }
    }

    // TODO    有点问题   从mongodb中取
    //判断是否双向喜欢
    public Boolean isLike(Long userId, Long likeUserId) {
        String key = Constants.USER_LIKE_KEY + userId;
        return redisTemplate.opsForSet().isMember(key, likeUserId.toString());
    }

    /**
     * 不喜欢
     */
    public void notLikeUser(Long likeUserId) {
        //当前登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1、调用API，保存喜欢数据(保存到MongoDB中)
        Boolean save = userLikeServiceApi.saveOrUpdate(userId,likeUserId,false);
        if(!save) {
            //失败
            throw new ConsumerException("不喜欢失败");
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY+userId,likeUserId.toString());
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY+userId,likeUserId.toString());
        //3. 判断是否双向喜欢，删除好友(各位自行实现)
        if (!isLike(likeUserId, userId)) {
            //4、删除好友
            messagesService.deleteContacts(likeUserId);
        }

    }

    //搜附近
    public List<NearUserVo> queryNearUser(String gender, String distance) {

        Long currentId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1、调用API查询附近的用户（返回的是附近的人的所有用户id，包含当前用户的id）
        List<Long> userIds = userLocationServiceApi.queryNearUser(currentId,Double.valueOf(distance));

        //2、判断集合是否为空
        if(CollUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }

        //3、调用UserInfoApi根据用户id查询用户详情
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        Map<Long, UserInfo> map = userInfoServiceApi.findByIds(userIds, userInfo);

        //4、构造返回值。
        List<NearUserVo> vos = new ArrayList<>();
        for (Long userId : userIds) {
            //排除当前用户
            if(userId.equals(currentId)) {
                continue;
            }
            UserInfo info = map.get(userId);
            if(info != null) {
                NearUserVo vo = NearUserVo.init(info);
                vos.add(vo);
            }
        }
        return vos;
    }
}
