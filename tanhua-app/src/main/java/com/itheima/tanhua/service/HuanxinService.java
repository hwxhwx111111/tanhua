package com.itheima.tanhua.service;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.vo.db.HuanxinVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HuanxinService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private UserServiceApi userServiceApi;

    /**
     * @description: 查询环信用户信息
     * @author: 黄伟兴
     * @date: 2022/9/28 22:08
     * @param: []
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.db.HuanxinVo>
     **/
    public HuanxinVo findHuanxinUser() {
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        User user  = userServiceApi.findHuanxinById(userId);
        if(user==null){
            throw new ConsumerException("用户不存在");
        }
        return new HuanxinVo(user.getHxUser(),user.getHxPassword());
    }
}
