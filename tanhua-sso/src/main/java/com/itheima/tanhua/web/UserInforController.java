package com.itheima.tanhua.web;

import com.itheima.tanhua.dto.db.UserInfoDto;
import com.itheima.tanhua.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
public class UserInforController {

    @Autowired
    private UserInfoService userInfoService;



    /**
     * @description: 03 首次登录---完善用户的资料
     * @author: 黄伟兴
     * @date: 2022/9/20 9:45
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestHeader("Authorization") String token, @RequestBody UserInfoDto userInfoDto) {
        userInfoService.loginReginfo(token,userInfoDto);
        return ResponseEntity.ok(null);
    }

    /**
     * @description: 上传头像
     * @author: 黄伟兴
     * @date: 2022/9/19 19:51
     * @param: [token, headPhoto]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("loginReginfo/head")
    public ResponseEntity uploadImage(@RequestHeader("Authorization") String token, MultipartFile headPhoto) {
        userInfoService.uploadImage(token,headPhoto);
        return ResponseEntity.ok(null);
    }

}
