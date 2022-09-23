package com.itheima.tanhua.api.db;

import com.itheima.tanhua.pojo.db.UserInfo;

public interface UserInfoServiceApi {


    /**
     * @description: 添加用户详细信息
     * @author: 黄伟兴
     * @date: 2022/9/20 9:45
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    void save(UserInfo userInfo);

    /**
     * @description: 保存用户头像地址
     * @author: 黄伟兴
     * @date: 2022/9/20 11:34
     * @param: [userInfo]
     * @return: void
     **/
    void updateAvatar(UserInfo userInfo);

    /**
     * @description: 根据用户id查询用户信息
     * @author: 黄伟兴
     * @date: 2022/9/23 0:17
     * @param: [userId]
     * @return: com.itheima.tanhua.pojo.db.UserInfo
     **/
    UserInfo findById(Long userId);
}
