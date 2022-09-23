package com.itheima.tanhua.service;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.autoconfig.template.OssTemplate;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//业务逻辑处理类
@Service
public class UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //@Autowired
    // private SmsTemplate smsTemplate;

    @DubboReference
    private UserServiceApi userServiceApi;

    @Autowired
    private OssTemplate ossTemplate;


    /**
     * @description: 发送短信验证码
     * @author: 黄伟兴
     * @date: 2022/9/19 11:18
     * @param: [phone]
     * @return: void
     **/
    public boolean sendMsg(String phone) {
        String redisKey = "CODE_" + phone;

        //1. 判断redis是否已经存在验证码了
        String code = redisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isNotBlank(code)) {
            //throw new RuntimeException("验证码已经存在");
            return false;
        }

        //生成6位验证码
        String identifyCode = RandomStringUtils.randomNumeric(6);
        System.out.println("短信验证码：" + identifyCode);

        //2.调用阿里sms服务向手机发送验证
        //smsTemplate.sendSms(phone,identifyCode);
        //验证码固定写死
        identifyCode = "111111";

        //3.将验证码存储到redis，有效期为5分钟
        redisTemplate.opsForValue().set(redisKey, identifyCode, 5, TimeUnit.MINUTES);
        return true;
    }

    /**
     * @description: 02 登录---验证码校验
     * @author: 黄伟兴
     * @date: 2022/9/19 17:15
     * @param: [phone, verificationCode]
     * @return: java.util.HashMap
     **/
    public HashMap loginVerification(String phone, String verificationCode) {
        String redisKey = "CODE_" + phone;
        boolean isNew = false;

        //1.从redis获取验证码
        String code = redisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new ConsumerException("验证码不存在");
        }

        //2.比较验证码
        if (!StrUtil.equals(code, verificationCode)) {
            throw new ConsumerException("验证码不一致");
        }

        //3.根据手机号查询用户，远程调用
        User user = userServiceApi.findByPhone(phone);
        if (ObjectUtil.isNull(user)) {
            //新用户，自动注册
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
         /*   user.setCreated(new Date());
            user.setUpdated(new Date());*/

            userServiceApi.save(user);
            //TODO，重新查询，获取id
            user = userServiceApi.findByPhone(phone);
            isNew = true;
        }

        //4.生成token，保存用户id
        String token = AppJwtUtil.getToken(user.getId());

        HashMap<Object, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("isNew", isNew);

        //5.清除验证码
        redisTemplate.delete(redisKey);
        return map;
    }





    /**
     * @description: 抽取token，获取用户id
     * @author: 黄伟兴
     * @date: 2022/9/20 11:47
     * @param: [token]
     * @return: java.lang.Long
     **/
    public Long getUserId(String token){
        //1.校验token
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        int i = AppJwtUtil.verifyToken(claimsBody);
        if (i == 1 || i == 2) {
            throw new ConsumerException("已经过期");
        }
        return Convert.toLong(claimsBody.get("id"));
    }
}
