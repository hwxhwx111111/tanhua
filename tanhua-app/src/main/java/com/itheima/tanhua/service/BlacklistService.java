package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.api.db.BlackListApi;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.pojo.db.BlackList;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.vo.db.PageResult;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BlacklistService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @DubboReference
    private BlackListApi blackListApi;
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    /**
     * 查询黑名单列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findBlacklist(String page, String pagesize) {
        String uid = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");
        //根据当前用户查询对应黑名单
        IPage<BlackList> iPage = blackListApi.findBlacklist(Long.valueOf(uid), Integer.valueOf(page), Integer.valueOf(pagesize));
        List<BlackList> records = iPage.getRecords();
        //提取所有黑名单用户的id
        List<Long> blackUserIds = CollUtil.getFieldValues(records, "blackUserId", Long.class);
        Map<Long, UserInfo> map = userInfoServiceApi.findbyIds(blackUserIds, null);

        List userInfos = new ArrayList<>();
        //封装分页对象
        for (Long blackUserId : blackUserIds) {
            UserInfo userInfo = map.get(blackUserId);
            if (ObjectUtil.isNotNull(userInfo)) {
                userInfos.add(userInfo);
            }
        }
        if (CollUtil.isEmpty(userInfos)) {
            return new PageResult();
        }
        PageResult<BlackList> pageResult = new PageResult<>();
        pageResult.setPage(Integer.valueOf(page));
        pageResult.setPagesize(Integer.valueOf(pagesize));
        pageResult.setCounts(Integer.valueOf(StrUtil.toString(iPage.getTotal())));
        pageResult.setItems(userInfos);
        //int count = (records.size() + Integer.valueOf(pagesize) - 1) / (Integer.valueOf(pagesize));
        pageResult.setPages(Integer.valueOf(StrUtil.toString(iPage.getPages())));
        return pageResult;
    }

    /**
     * 移除黑名单
     *
     * @param uid
     */
    public void deleteblacklist(String uid) {
        //获取当前登录用户的id;
        String userid = stringRedisTemplate.opsForValue().get("AUTH_USER_ID");
        //根据当前用户id以及所选用户的id去黑名单表查询
        BlackList blackList = blackListApi.selectBlacklist(userid, uid);
        //如果存在删除数据
        if (ObjectUtil.isNotNull(blackList)) {
            blackListApi.deleteblacklist(userid, uid);
        }
    }

}
