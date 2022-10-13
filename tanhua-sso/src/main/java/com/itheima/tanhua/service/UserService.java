package com.itheima.tanhua.service;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.autoconfig.template.HuanXinTemplate;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.Interface.LogConfig;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.utils.AppJwtUtil;
import com.itheima.tanhua.utils.Constants;
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
    @Autowired
    private UserFreezeService userFreezeService;
    @Autowired
    private MqMessageService mqMessageService;
    @DubboReference
    private UserServiceApi userServiceApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;


    /**
     * @description: 发送短信验证码
     * @author: 黄伟兴
     * @date: 2022/9/19 11:18
     * @param: [phone]
     * @return: void
     **/
    public boolean sendMsg(String phone) {
        //根据手机号查询用户，如果用户存在，判断是否被冻结
        User user = userServiceApi.findByPhone(phone);
        if(user != null) {
            userFreezeService.checkUserStatus("1",user.getId());
        }
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
        String type = "0101";
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
            type = "0102";
            //新用户，自动注册
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            userServiceApi.save(user);
            user = userServiceApi.findByPhone(phone);
            isNew = true;


            //注册环信用户
            String hxUser = "hx" + user.getId();
            Boolean create = huanXinTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            System.out.println("create: " + create);
            if (create) {
                //如果注册成功，将用户信息补充
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userServiceApi.update(user);
            }

        }
        mqMessageService.sendLogService(user.getId(),type,"user",null);
        //4.生成token，保存用户id
        String token = AppJwtUtil.getToken(user.getId());

        HashMap<Object, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("isNew", isNew);

        //5.清除验证码
        redisTemplate.delete(redisKey);
        return map;
    }

}
