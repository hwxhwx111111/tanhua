package com.itheima.tanhua.web;


import com.itheima.tanhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @description: ：01 登录---根据手机号获取验证码
     * @author: 黄伟兴
     * @date: 2022/9/19 11:16
     * @param: [map]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> map) {
        boolean flag = userService.sendMsg(map.get("phone"));
        if (!flag) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("验证码还未失效");
        }

        return ResponseEntity.ok(null);
    }

    /**
     * @description: 02 登录---验证码校验
     * @author: 黄伟兴
     * @date: 2022/9/19 17:14
     * @param: [param]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String, String> param) {
        String phone = param.get("phone");
        String verificationCode = param.get("verificationCode");
        HashMap map = userService.loginVerification(phone, verificationCode);
        return ResponseEntity.ok(map);
    }




    /**
     * @description: 向外提供一个获取用户id
     * @author: 黄伟兴
     * @date: 2022/9/20 13:59
     * @param: [token]
     * @return: java.lang.Long
     **/
    @GetMapping("{token}")
    public Long getUserId(@PathVariable String token){
        return userService.getUserId(token);
    }

}
