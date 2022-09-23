package com.itheima.tanhua.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.TodayBestVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendService {

    @Autowired
    private RestTemplate restTemplate;


    @DubboReference
    private RecommendServiceApi recommendServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;


    public TodayBestVo todayBest(String token) {
        //1.校验token,获取当前登录者id
        String url = "http://tanhua-sso/user/" + token;
        Long uid = restTemplate.getForObject(url, Long.class);

        //2.获取mongo中缘分值
        RecommendUser recommendUser = recommendServiceApi.todayBest(uid);

        //3.获取佳人的信息，mysql中
        UserInfo userInfo = userInfoServiceApi.findById(recommendUser.getUserId());

        TodayBestVo todayBestVo = new TodayBestVo();
        //4.组装信息
        BeanUtils.copyProperties(userInfo, todayBestVo);

        //处理标签
        todayBestVo.setTags(StrUtil.split(userInfo.getTags(), ","));
        todayBestVo.setFateValue(Convert.toInt(recommendUser.getScore()));


        return todayBestVo;
    }

    /**
     * @description: 佳人推荐
     * @author: 黄伟兴
     * @date: 2022/9/23 1:50
     * @param: [token, page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    public PageResult recommendation(String token, Integer page, Integer pagesize) {

        //1.校验token,获取当前登录者id
        String url = "http://tanhua-sso/user/" + token;
        Long uid = restTemplate.getForObject(url, Long.class);

        //2.用uid到mongdb中查询佳人的信息
        List<RecommendUser> list = recommendServiceApi.findByToUId(uid);


        //
        List<Object> listVo = new ArrayList<>();

        //4.遍历list，
        for (RecommendUser recommendUser : list) {
            Long userId = recommendUser.getUserId();
            //3.获取佳人的信息，mysql中
            UserInfo userInfo = userInfoServiceApi.findById(recommendUser.getUserId());

            TodayBestVo todayBestVo = new TodayBestVo();
            //4.组装信息
            BeanUtils.copyProperties(userInfo, todayBestVo);

            //处理标签
            todayBestVo.setTags(StrUtil.split(userInfo.getTags(), ","));
            todayBestVo.setFateValue(Convert.toInt(recommendUser.getScore()));

            listVo.add(todayBestVo);

        }
        return new PageResult(page,pagesize,listVo);
    }
}
