package com.itheima.tanhua.api.mongo;


import com.itheima.tanhua.pojo.mongo.PeachBlossom;

public interface PeachBlossomApi {
    void save(PeachBlossom peachBlossom);

    PeachBlossom getOne(Long userId);
}
