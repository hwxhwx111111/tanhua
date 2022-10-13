package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.UserLike;

import java.util.List;

public interface UserLikeServiceApi {


    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean b);


    boolean isLike(String uid, Long userId);

    List<UserLike> fendByIdLove(Long userId);

    List<UserLike> fendByIdLike(Long userId);

    List<UserLike> selectLove(Long userId);

    List<UserLike> selectFan(Long userId);

    List<UserLike> selectEachLove(Long userId);

    Boolean findFs(Long userId, Long fanUser);
}
