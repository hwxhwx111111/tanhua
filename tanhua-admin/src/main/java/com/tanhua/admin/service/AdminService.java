package com.tanhua.admin.service;


import cn.hutool.core.convert.Convert;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.tanhua.pojo.db.Admin;
import com.itheima.tanhua.utils.AppJwtUtil;
import com.itheima.tanhua.utils.Constants;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * @description: 用户登录
     * @author: 黄伟兴
     * @date: 2022/10/11 16:16
     * @param: [map]
     * @return: java.util.Map
     **/
    public Map<String,String> login(Map<String,String> map) {

        //1、获取请求的参数（username,password,verificationCode（验证码）,uuid）
        String username =  map.get("username");
        String password =  map.get("password");
        String verificationCode =  map.get("verificationCode");
        String uuid = map.get("uuid");

        //2、判断用户名或者密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            //用户名或者密码为空
            throw new BusinessException("用户名或者密码为空");
        }

        //3、判断验证码是否正确
        if (StringUtils.isEmpty(verificationCode)) {
            //验证码为空
            throw new BusinessException("验证码为空");
        }

        //从redis中获取验证码
        String key = Constants.CAP_CODE+uuid;
        String code = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(code) || !code.equals(verificationCode)) {
            //验证码错误
            throw new BusinessException("验证码错误");
        }
        redisTemplate.delete(key);

        //4、根据用户名查询用户
        QueryWrapper<Admin> qw = new QueryWrapper<>();
        qw.eq("username", username);
        Admin admin = adminMapper.selectOne(qw);
        //5、判断用户是否存在，密码是否一致
        password = SecureUtil.md5(password); //md5加密
        if(admin == null || !admin.getPassword().equals(password)) {
            //用户名错误或者密码不一致
            throw new BusinessException("用户名或者密码");
        }

        //6、通过JWT生成token
        String token = AppJwtUtil.getToken(admin.getId());
        //8、构造返回值
        Map<String,String> res = new HashMap<String,String>();
        res.put("token", token);

        return res;
    }

    /**
     * @description: 用户基本信息
     * @author: 黄伟兴
     * @date: 2022/10/11 16:20
     * @param: []
     * @return: com.itheima.tanhua.pojo.db.Admin
     **/
    public Admin profile() {
        Long id = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        Admin admin = adminMapper.selectById(id);
        return admin;
    }
}
