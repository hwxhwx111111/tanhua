package com.itheima.tanhua.api.db;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.pojo.db.BlackList;



public interface BlackListApi {

    IPage<BlackList> findBlacklist(Long uid, Integer page, Integer pagesize);

    BlackList selectBlacklist(String userid, String uid);

    void deleteblacklist(String userid, String uid);
}
