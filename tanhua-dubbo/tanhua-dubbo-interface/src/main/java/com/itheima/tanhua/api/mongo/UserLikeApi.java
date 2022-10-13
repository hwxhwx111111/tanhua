package com.itheima.tanhua.api.mongo;

public interface UserLikeApi {


    Boolean saveOrUpdate(Long valueOf, Long likeUserId, boolean b);
}
