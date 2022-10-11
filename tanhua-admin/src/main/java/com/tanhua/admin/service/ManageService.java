package com.tanhua.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.dto.db.FreezeDto;
import com.itheima.tanhua.enums.FreezingTime;
import com.itheima.tanhua.enums.UserStatus;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.db.UsersInfoVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ManageService {
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @Autowired
    private StringRedisTemplate redisTemplate;


    public PageResult findPageUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> ipage = userInfoServiceApi.findPageUsers(page, pagesize);
        long total = ipage.getTotal();
        List<UsersInfoVo> list = new ArrayList<>();
        for (UserInfo record : ipage.getRecords()) {
            UsersInfoVo vo = new UsersInfoVo();
            BeanUtil.copyProperties(record, vo);
            String userStatus = redisTemplate.opsForValue().get(Constants.USER_FREEZE + record.getId());
            if (StrUtil.equals(userStatus, UserStatus.FREEZE.getType())) {
                vo.setUserStatus(UserStatus.FREEZE.getType());
            }
            list.add(vo);
        }
        PageResult<UsersInfoVo> result = new PageResult<>(page, pagesize, total, list);
        return result;
    }

    public UsersInfoVo findByUserId(String userID) {
        //根据userID查询用户详情
        UserInfo userInfo = userInfoServiceApi.findById(Convert.toLong(userID));

        //封装为UsersInfoVo
        UsersInfoVo usersInfoVo = new UsersInfoVo();
        BeanUtil.copyProperties(userInfo,usersInfoVo);
        //判断是否为冻结状态
        String userStatus = redisTemplate.opsForValue().get(Constants.USER_FREEZE + userID);
        if (StrUtil.equals(userStatus,UserStatus.FREEZE.getType())){
            //如果为冻结状态则将userStatus设置为2
            usersInfoVo.setUserStatus(UserStatus.FREEZE.getType());
        }
        return usersInfoVo;
    }

    public void freeze(FreezeDto freezeDto) {
        //将冻结详情放入redis中
        String hashKey = Constants.FREEZE_USER+freezeDto.getUserId();
        redisTemplate.opsForHash().put(hashKey,"freezingTime",Convert.toStr(freezeDto.getFreezingTime()));
        redisTemplate.opsForHash().put(hashKey,"freezingRange",Convert.toStr(freezeDto.getFreezingRange()));
        redisTemplate.opsForHash().put(hashKey,"reasonsForFreezing",freezeDto.getReasonsForFreezing());
        redisTemplate.opsForHash().put(hashKey,"frozenRemarks",freezeDto.getFrozenRemarks());
        //根据freezingTime设置冻结时间
        if (FreezingTime.THREE_DAY.getType() == freezeDto.getFreezingTime()){
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType(),3, TimeUnit.DAYS);
        }else  if (FreezingTime.ONE_WEEK.getType() == freezeDto.getFreezingTime()){
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType(),7, TimeUnit.DAYS);
        }else{
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType());
        }
    }

    public void unfreeze(Integer userId, String frozenRemarks) {
        //将解封信息存入redis中
        redisTemplate.opsForHash().put(Constants.FREEZE_USER+userId,"frozenRemarks",frozenRemarks);
        //修改redis中的冻结信息
        redisTemplate.delete(Constants.USER_FREEZE+userId);
    }
}
