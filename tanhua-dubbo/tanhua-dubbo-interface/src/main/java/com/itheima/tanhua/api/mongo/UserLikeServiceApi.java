package com.itheima.tanhua.api.mongo;

public interface UserLikeServiceApi {

    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean b);
}
