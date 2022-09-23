package com.itheima.tanhua.service;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.autoconfig.template.AipFaceTemplate;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.dto.db.UserInfoDto;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private AipFaceTemplate aipFaceTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * @description: 03 首次登录---完善用户的资料
     * @author: 黄伟兴
     * @date: 2022/9/20 9:45
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    public void loginReginfo(UserInfoDto userInfoDto) {
        String id = redisTemplate.opsForValue().get("AUTH_USER_ID");

        //3.组装用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(Convert.toLong(id));
        BeanUtils.copyProperties(userInfoDto, userInfo);


        //4.保存用户详细信息
        userInfoServiceApi.save(userInfo);
        //销毁用户id
      //  redisTemplate.delete("AUTH_USER_ID");
    }

    /**
     * @description: 上传头像
     * @author: 黄伟兴
     * @date: 2022/9/19 19:51
     * @param: [token, headPhoto]
     * @return: org.springframework.http.ResponseEntity
     **/
    public void uploadImage(MultipartFile headPhoto) {
        //1.校验token并获取id
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");

        //2.调用上传功能
        try {
            //1.将图片上传到阿里云OSS中
            String imagePath = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

            //2.添加人脸识别，检测当前头像是否是人像，如果不是头像，就抛出异常，
            //方式2
            boolean b = aipFaceTemplate.detect(imagePath);
            if (!b) {
                throw new ConsumerException("上传头像错误，非人像");
            }

            //2.将头像地址写到userInfo中
            UserInfo userInfo = new UserInfo();
            userInfo.setId(Convert.toLong(userId));
            userInfo.setAvatar(imagePath);
            userInfoServiceApi.updateAvatar(userInfo);
        } catch (IOException e) {
            throw new ConsumerException("头像上传失败啦！");
        }

        //销毁用户id
       // redisTemplate.delete("AUTH_USER_ID");
    }

}
