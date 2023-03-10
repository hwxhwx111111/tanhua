package com.tanhua.admin.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itheima.tanhua.pojo.db.Admin;
import com.itheima.tanhua.utils.Constants;
import com.tanhua.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * @description: 生成图片验证码
     * @author: 黄伟兴
     * @date: 2022/10/11 16:16
     * @param: [uuid, response]
     * @return: void
     **/
    @GetMapping("/verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        //设置响应参数
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //1、通过工具类生成验证码对象（图片数据和验证码信息）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(299, 97);

        //2、调用service，将验证码存入到redis
        String code = captcha.getCode();  //1234
        //System.out.println("验证码："+code);
        redisTemplate.opsForValue().set(Constants.CAP_CODE+uuid,code);

        //3、通过输出流输出验证码
        captcha.write(response.getOutputStream());
    }

    /**
     * @description: 登录验证
     * @author: 黄伟兴
     * @date: 2022/10/11 16:15
     * @param: [map]
     * @return: org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.String>>
     **/
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> map) {
        Map<String,String> retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * @description: 用户基本信息
     * @author: 黄伟兴
     * @date: 2022/10/11 16:15
     * @param: []
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("profile")
    public ResponseEntity profile(){
        Admin admin=adminService.profile();
        return ResponseEntity.ok(admin);

    }

    /**
     * @description: 退出登录
     * @author: 黄伟兴
     * @date: 2022/10/11 16:15
     * @param: []
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("logout")
    public ResponseEntity logout(){
        //清除redis中的用户id
        redisTemplate.delete(Constants.USER_ID);
        return ResponseEntity.ok(null);
    }


}
