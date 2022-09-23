package com.itheima.tanhua.service;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.autoconfig.template.AipFaceTemplate;
import com.itheima.tanhua.autoconfig.template.OssTemplate;
import com.itheima.tanhua.dto.db.UserInfoDto;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;


    /**
     * @description: 03 首次登录---完善用户的资料
     * @author: 黄伟兴
     * @date: 2022/9/20 9:45
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    public void loginReginfo(String token, UserInfoDto userInfoDto) {
    /*    //1.校验token
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        int i = AppJwtUtil.verifyToken(claimsBody);
        if (i == 1 || i == 2) {
            throw new ConsumerException("已经过期");
        }
        //2.获取用户id
        Object id = claimsBody.get("id");
        */
        //1，2调用抽取出来的工具类
        Long id = userService.getUserId(token);

        //3.组装用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(Convert.toLong(id));
        BeanUtils.copyProperties(userInfoDto, userInfo);


        //4.保存用户详细信息
        userInfoServiceApi.save(userInfo);
    }

    /**
     * @description: 上传头像
     * @author: 黄伟兴
     * @date: 2022/9/19 19:51
     * @param: [token, headPhoto]
     * @return: org.springframework.http.ResponseEntity
     **/
    public void uploadImage(String token, MultipartFile headPhoto) {
        //1.校验token
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        int i = AppJwtUtil.verifyToken(claimsBody);
        if (i == 1 || i == 2) {
            throw new ConsumerException("已经过期");

        }

        //2.调用上传功能
        try {
            //1.将图片上传到阿里云OSS中
            String imagePath = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

            //2.添加人脸识别，检测当前头像是否是人像，如果不是头像，就抛出异常，

            //方式1 ，使用工具类
         /*   Integer code = FaceUtil.checkFace(imagePath);
            if (code != 0) {
                throw new ConsumerException("上传头像错误");
            }*/

            //方式2
            boolean b = aipFaceTemplate.detect(imagePath);
            if (!b) {
                throw new ConsumerException("上传头像错误，非人像");
            }

            //2.将头像地址写到userInfo中
            UserInfo userInfo = new UserInfo();
            userInfo.setId(Convert.toLong(claimsBody.get("id")));
            userInfo.setAvatar(imagePath);
            userInfoServiceApi.updateAvatar(userInfo);
        } catch (IOException e) {
            throw new ConsumerException("头像上传失败啦！");
        }
    }


}
