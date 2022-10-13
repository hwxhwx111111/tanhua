package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.autoconfig.template.AipFaceTemplate;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.db.UserServiceApi;
import com.itheima.tanhua.api.mongo.FriendServiceApi;
import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.api.mongo.UserLikeServiceApi;
import com.itheima.tanhua.api.mongo.VisitorsServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.User;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Friend;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.pojo.mongo.UserLike;
import com.itheima.tanhua.pojo.mongo.Visitors;
import com.itheima.tanhua.utils.AppJwtUtil;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.db.PageResultM;
import com.itheima.tanhua.vo.db.UserInfoBVo;
import com.itheima.tanhua.vo.db.UserInfoCVo;
import com.itheima.tanhua.vo.mongo.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private UserServiceApi userServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @DubboReference
    private UserLikeServiceApi userLikeServiceApi;

    @DubboReference
    private FriendServiceApi friendServiceApi;

    @DubboReference
    private VisitorsServiceApi visitorsServiceApi;

    @DubboReference
    private RecommendServiceApi recommendServiceApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    //读取用户信息
    public UserInfoBVo findById() {
        String id = redisTemplate.opsForValue().get("AUTH_USER_ID");
        UserInfo byId = userInfoServiceApi.findById(Long.parseLong(id));
        UserInfoBVo userInfoBVo = new UserInfoBVo();
        BeanUtils.copyProperties(byId,userInfoBVo);
        userInfoBVo.setAge(byId.getAge()+"");
        userInfoBVo.setId(Integer.parseInt(byId.getId()+""));
        userInfoBVo.setMarriage(byId.getMarriage());
        return userInfoBVo;
    }

    //保存用户资料
    public void saveById(UserInfo userInfo) {
        String id = redisTemplate.opsForValue().get("AUTH_USER_ID");
        userInfo.setId(Long.parseLong(id));
        try {
            userInfoServiceApi.updateUser(userInfo);
        } catch (Exception e) {
            throw new RuntimeException("保存失败");
        }
    }
    //修改用户头像
    public void updateHeadPhoto(MultipartFile headPhoto) {
        //1.校验token并获取id
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");
        //2.调用上传功能
        try {
            //1.将图片上传到阿里云OSS中
            String imagePath = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
            //2.将头像地址写到userInfo中
            UserInfo userInfo = new UserInfo();
            userInfo.setId(Convert.toLong(userId));
            userInfo.setAvatar(imagePath);
            userInfoServiceApi.updateAvatar(userInfo);
        } catch (IOException e) {
            throw new ConsumerException("头像上传失败啦！");
        }
    }

    //查询用户好友,喜欢,粉丝数量
    public RelationVo findCounts() {
        String userIds = redisTemplate.opsForValue().get("AUTH_USER_ID");
        Long userId = Long.parseLong(userIds);
        //查询喜欢
        List<UserLike> userLikes = userLikeServiceApi.selectLove(userId);
        //查询粉丝
        List<UserLike> userLikes1 = userLikeServiceApi.selectFan(userId);
        //查询相互喜欢
        List<UserLike> userEachLove = userLikeServiceApi.selectEachLove(userId);

        RelationVo relationVo = new RelationVo();
        relationVo.setEachLoveCount(userEachLove.size());
        relationVo.setLoveCount(userLikes.size());
        relationVo.setFanCount(userLikes1.size());
        return relationVo;
    }

    //查询用户好友,喜欢,粉丝,看我的人详细信息
    public PageResultM<UserInfoCVo> friends(Integer page, Integer pagesize, Long type) {
        String userIds = redisTemplate.opsForValue().get("AUTH_USER_ID");
        Long userId = Long.parseLong(userIds);
        List<UserLike> userLikes = new ArrayList<>();
        if (type==1){
            userLikes = userLikeServiceApi.selectEachLove(userId);;
        }else
        if (type==2){
            userLikes = userLikeServiceApi.selectLove(userId);
        }else
        if (type==3){
            userLikes = userLikeServiceApi.selectFan(userId);
        }else
        if (type==4){
            String currentUserId = redisTemplate.opsForValue().get("AUTH_USER_ID");
            //1.查询访问时间
            String key = Constants.VISITORS_USER;
            String hashKey = currentUserId;
            String value = (String) redisTemplate.opsForHash().get(key, hashKey);
            Long date = StringUtils.isEmpty(value) ? null : Long.valueOf(value);
            //2.调用API查询数据列表 List<Visitors>
            List<Visitors> list = visitorsServiceApi.queryMyVisitors(date, Convert.toLong(currentUserId));
            if (CollUtil.isEmpty(list)) {
                return null;
            }
            List<UserInfoCVo> userInfoCVos = new ArrayList<>();
            for (Visitors visitors : list) {
                Long userId1 = visitors.getVisitorUserId();
                UserInfo byId = userInfoServiceApi.findById(userId1);
                RecommendUser byId1 = recommendServiceApi.findById(userId1,userId);
                //判断是否是我的粉丝
                Boolean bo= userLikeServiceApi.findFs(userId,userId1);
                if (byId == null) {
                    throw new ConsumerException("没有数据");
                }
                //4.组装信息
                UserInfoCVo userInfo = new UserInfoCVo();
                BeanUtils.copyProperties(byId,userInfo);

                if (byId1==null){
                    userInfo.setMatchRate(60);
                }else {
                    long round = Math.round(byId1.getScore());
                    userInfo.setMarriage(Integer.parseInt(round+""));
                }
//                userInfo.setMarriage(Integer.parseInt(byId1.getScore()+""));
                userInfo.setAlreadyLove(bo);
                userInfoCVos.add(userInfo);
            }
            PageResultM<UserInfoCVo> userInfoCVoPageResultM = new PageResultM<UserInfoCVo>(page, pagesize,userInfoCVos);
            return userInfoCVoPageResultM;
        } else{
            throw new ConsumerException("参数有误");
        }

        List<UserInfoCVo> userInfoCVos = new ArrayList<>();
        for (UserLike userLike : userLikes) {
            Long userId1;
            if (type==2){
                userId1 = userLike.getLikeUserId();
            }else {
                userId1 = userLike.getUserId();
            }
            UserInfo byId = userInfoServiceApi.findById(userId1);
            RecommendUser byId1 = recommendServiceApi.findById(userId1, userId);
            Boolean bo= userLikeServiceApi.findFs(userId,userId1);

            if (byId == null) {
                throw new ConsumerException("没有数据");
            }
            //4.组装信息
            UserInfoCVo userInfo = new UserInfoCVo();
            BeanUtils.copyProperties(byId,userInfo);
            userInfo.setAlreadyLove(bo);
            userInfo.setId(Integer.parseInt(userId1+""));
            if (byId1==null){
                userInfo.setMatchRate(60);
            }else {
                long round = Math.round(byId1.getScore());
                userInfo.setMatchRate(Integer.parseInt(round+""));
            }
            userInfoCVos.add(userInfo);
        }
        PageResultM<UserInfoCVo> userInfoCVoPageResultM = new PageResultM<UserInfoCVo>(page, pagesize,userInfoCVos);
        return userInfoCVoPageResultM;
    }

    //发送手机验证码
    public boolean sendMsg() {
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");
        User byId = userServiceApi.findById(userId);
        String redisKey = "CODE_" + byId.getMobile();
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

    //检查验证码
    public Boolean jcVerificationCode(String verificationCode) {
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");
        User byId = userServiceApi.findById(userId);
        String redisKey = "CODE_" + byId.getMobile();
        boolean isNew = false;
        //1.从redis获取验证码
        String code = redisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            return false;
        }
        //2.比较验证码
        if (!StrUtil.equals(code, verificationCode)) {
            return false;
        }
        //5.清除验证码
        redisTemplate.delete(redisKey);
        return true;
    }
    //保存手机号
    public void savePhone(String phone) {
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");
        User user = new User();
        user.setId(Long.parseLong(userId));
        user.setMobile(phone);
        userServiceApi.update(user);
    }
}
