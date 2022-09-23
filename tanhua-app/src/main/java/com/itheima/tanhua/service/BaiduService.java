package com.itheima.tanhua.service;


import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.mongo.UserLocationServiceApi;
import com.itheima.tanhua.exception.BusinessException;
import com.itheima.tanhua.exception.ErrorResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @DubboReference
    private UserLocationServiceApi userLocationApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //更新地理位置
    public void updateLocation(Double longitude, Double latitude, String address) {
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        Boolean flag = userLocationApi.updateLocation(userId,longitude,latitude,address);
        if(!flag) {
            throw  new BusinessException(ErrorResult.error());
        }
    }
}
