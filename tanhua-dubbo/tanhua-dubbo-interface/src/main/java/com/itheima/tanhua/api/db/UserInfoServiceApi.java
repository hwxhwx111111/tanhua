package com.itheima.tanhua.api.db;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.pojo.db.UserInfo;

import java.util.List;
import java.util.Map;

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
     * @description: 根据用户id查询用户详细信息
     * @author: 黄伟兴
     * @date: 2022/9/24 19:40
     * @param: [userId]
     * @return: com.itheima.tanhua.pojo.db.UserInfo
     **/
    UserInfo findById(Long userId);

    /**
     * @description: 通过id查询用户信息
     * @author: 黄伟兴
     * @date: 2022/9/29 1:12
     * @param: [userIds, o]
     * @return: java.util.Map<java.lang.Long,com.itheima.tanhua.pojo.db.UserInfo>
     **/
    Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo userInfo);

    /**
     * @description: 联系人列表分页查询
     * @author: 黄伟兴
     * @date: 2022/9/29 1:12
     * @param: [friendIds, page, pagesize, keyword]
     * @return: java.util.List<com.itheima.tanhua.pojo.db.UserInfo>
     **/
    List<UserInfo> findPageByIds(List<Long> friendIds, Integer page, Integer pagesize, String keyword);

    /**
     * 分页查询用户信息
     * @param page
     * @param pagesize
     * @return
     */
    IPage<UserInfo> findPageUsers(Integer page, Integer pagesize);

    Map<Long, UserInfo> getUserInfoMap(List<Long> userId, UserInfo condition);

    List<UserInfo> getUserInfo(List<Long> userIds, UserInfo condition);

    Map<Long, UserInfo> findbyIds(List<Long> userIds, UserInfo userInfo);

    void updateUser(UserInfo userInfo);
}
