package com.tanhua.admin.service;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class ManageService {
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    public PageResult findPageUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> ipage = userInfoServiceApi.findPageUsers(page,pagesize);
        long total = ipage.getTotal();
        PageResult<UserInfo> result = new PageResult<>(page, pagesize, total, ipage.getRecords());
        return result;
    }
}
