package com.itheima.tanhua.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.tanhua.api.db.BlackListApi;
import com.itheima.tanhua.mapper.BlackListMapper;
import com.itheima.tanhua.pojo.db.BlackList;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private BlackListMapper blackListMapper;

    /**
     * 查询黑名单列表
     *
     * @param uid
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public IPage<BlackList> findBlacklist(Long uid, Integer page, Integer pagesize) {
        Page<BlackList> blackListPage = new Page<>(page, pagesize);
        LambdaQueryWrapper<BlackList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlackList::getUserId, uid);
        return blackListMapper.selectPage(blackListPage, wrapper);
    }

    @Override
    public BlackList selectBlacklist(String userid, String uid) {
        LambdaQueryWrapper<BlackList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlackList::getUserId, Long.valueOf(userid));
        wrapper.eq(BlackList::getBlackUserId, Long.valueOf(uid));
        return blackListMapper.selectOne(wrapper);
    }

    @Override
    public void deleteblacklist(String userid, String uid) {
        QueryWrapper<BlackList> qw = new QueryWrapper<>();
        qw.eq("user_id",userid);
        qw.eq("black_user_id",uid);
        blackListMapper.delete(qw);
    }
}
