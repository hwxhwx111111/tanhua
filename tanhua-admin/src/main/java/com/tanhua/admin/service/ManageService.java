package com.tanhua.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
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
}
