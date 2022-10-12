package com.itheima.tanhua.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.api.mongo.VisitorsServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.pojo.mongo.TodayBest;
import com.itheima.tanhua.pojo.mongo.Visitors;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.TodayBestVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RecommendService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private RecommendServiceApi recommendServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @DubboReference
    private VisitorsServiceApi visitorsServiceApi;


    /**
     * @description: 查询今日佳人
     * @author: 黄伟兴
     * @date: 2022/9/24 19:33
     * @param: [toUserId]
     * @return: com.itheima.tanhua.pojo.mongo.RecommendUser
     **/
    public TodayBestVo todayBest() {
        //1.校验token,获取当前登录者id
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");

        //2.从mongodb中获取佳人信息
        RecommendUser recommendUser = recommendServiceApi.todayBest(Convert.toLong(userId));

        //3.获取佳人的信息，mysql中
        UserInfo userInfo = userInfoServiceApi.findById(recommendUser.getUserId());
        if (userInfo == null) {
            throw new ConsumerException("没有佳人数据");
        }

        //4.组装信息
        TodayBestVo todayBestVo = new TodayBestVo();
        BeanUtils.copyProperties(userInfo, todayBestVo);
        //处理标签
        todayBestVo.setTags(StrUtil.split(userInfo.getTags(), ","));
        todayBestVo.setFateValue(Convert.toInt(recommendUser.getScore()));

        //清除redis
        redisTemplate.delete("AUTH_USER_ID");
        return todayBestVo;
    }

    /**
     * @description: 佳人推荐
     * @author: 黄伟兴
     * @date: 2022/9/23 1:50
     * @param: [token, page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    public PageResult<TodayBestVo> recommendation(Integer page, Integer pagesize) {

        //1.校验token,获取当前登录者id
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");  //高并发存在问题

        //2.用userId到mongdb中查询佳人的信息,并分页
        List<RecommendUser> recommendUsers = recommendServiceApi.findByToUId(Convert.toLong(userId), page, pagesize);

        //3.组装信息， 遍历list，
        List<TodayBestVo> listVo = new ArrayList<>();
        for (RecommendUser recommendUser : recommendUsers) {
            // 3.获取佳人的信息，mysql中
            UserInfo userInfo = userInfoServiceApi.findById(recommendUser.getUserId());

            //4.组装信息
            TodayBestVo todayBestVo = new TodayBestVo();
            BeanUtils.copyProperties(userInfo, todayBestVo);
            //处理标签
            todayBestVo.setTags(StrUtil.split(userInfo.getTags(), ","));
            todayBestVo.setFateValue(Convert.toInt(recommendUser.getScore()));

            listVo.add(todayBestVo);
        }
        //清除redis
        redisTemplate.delete("AUTH_USER_ID");

        return new PageResult<TodayBestVo>(page, pagesize, 0L, listVo);
    }

    /**
     * @description: 查看佳人详情
     * @author: 黄伟兴
     * @date: 2022/9/29 0:23
     * @param: [userId]
     * @return: com.itheima.tanhua.vo.mongo.TodayBestVo
     **/
    public TodayBest personalInfo(Long userId) {

        Long uid = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        //1、根据userId（当前点击的佳人id）查询用户信息
        UserInfo userInfo = userInfoServiceApi.findById(userId);
        //2、查询uid(当前登录者id)与userId（当前点击的佳人id）缘分值
        RecommendUser user = recommendServiceApi.findById(uid, userId);
        System.out.println(user);

        //当前登录者id
        Long currentUserId =Convert.toLong( redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //构造访客数据，调用API保存数据
        Visitors visitors = new Visitors();
        visitors.setUserId(userId);
        visitors.setVisitorUserId(currentUserId);
        visitors.setFrom("首页");
        visitors.setDate(System.currentTimeMillis());
        visitors.setVisitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));

        Long score = 80L;
        if (user!=null){
            score= Convert.toLong(user.getScore());
        }

        visitorsServiceApi.save(visitors);


        //3.构造返回值
        TodayBest vo = new TodayBest();
        BeanUtil.copyProperties(userInfo, vo);
        vo.setTags(StrUtil.split(userInfo.getTags(), ","));
        vo.setFateValue(score);


        //3、构造返回值
        return vo;
    }

    public List<RecommendUser> queryCardList(Long userId, int count) {
        return recommendServiceApi.queryCardList(userId, count);
    }
}
