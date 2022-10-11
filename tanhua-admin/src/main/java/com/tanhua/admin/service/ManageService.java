package com.tanhua.admin.service;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.MovementServiceApi;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Movement;
import com.itheima.tanhua.vo.mongo.MovementsVoNew;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageService {
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;


    private MovementServiceApi movementServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public PageResult findPageUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> ipage = userInfoServiceApi.findPageUsers(page,pagesize);
        long total = ipage.getTotal();
        PageResult<UserInfo> result = new PageResult<>(page, pagesize, total, ipage.getRecords());
        return result;
    }


    /**
     * @description: 后台-根据用户id和动态状态分页查询动态
     * @author: 黄伟兴
     * @date: 2022/10/11 16:40
     * @param: [page, pagesize, uid, state]
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.mongo.PageResult < com.itheima.tanhua.vo.mongo.MovementsVo>>
     **/
    public PageResult<MovementsVoNew> findMovementByIdAndState(Integer page, Integer pagesize, Long uid, Integer state) {
        //1. 获取id查询当前登录者的动态
        if (uid == null) {
            //当前登录者的id
            uid = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        }

        //2.当前用户发布的动态数据  从mongodb中
        List<Movement> movementList = movementServiceApi.findMovementByIdAndState(uid, state, page, pagesize);

        //4.调用方法，封装数据
        PageResult<MovementsVoNew> pageResult = getPageResultOfVoList1(page, pagesize, movementList);

        //清除redis
        // redisTemplate.delete("AUTH_USER_ID");

        return pageResult;
    }

    private PageResult<MovementsVoNew> getPageResultOfVoList1(Integer page, Integer pagesize, List<Movement> movementList) {
        return null;
    }

    /**
     * @description: 动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:22
     * @param: [movementId]
     * @return: java.lang.String
     **/
    public String setMovementTop(String movementId) {

        Boolean flag = movementServiceApi.setMovementTop(movementId);

        return flag ? "动态置顶成功！" : "动态置顶失败";
    }

    /**
     * @description: 取消动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:30
     * @param: [movementId]
     * @return: java.lang.String
     **/
    public String divestMovementTop(String movementId) {

        Boolean flag = movementServiceApi.divestMovementTop(movementId);

        return flag ? "取消动态置顶成功！" : "取消动态置顶失败";
    }

    /**
     * @description: 动态通过
     * @author: 黄伟兴
     * @date: 2022/10/11 17:46
     * @param: [movementId]
     * @return: com.itheima.tanhua.vo.mongo.MovementsVoNew
     **/
    public String approveMovement(String[] movementIds) {
        Boolean flag =   movementServiceApi.approveMovement(movementIds);

        return flag?"动态通过":"动态未通过";
    }

    /**
     * @description: 动态拒绝
     * @author: 黄伟兴
     * @date: 2022/10/11 17:57
     * @param: [movementIds]
     * @return: java.lang.String
     **/
    public String rejectMovement(String[] movementIds) {
        Boolean flag =   movementServiceApi.rejectMovement(movementIds);

        return flag?"动态拒绝成功！":"动态拒绝失败！";
    }

    /**
     * @description: 查询动态详情-后台
     * @author: 黄伟兴
     * @date: 2022/10/11 18:09
     * @param: [movementId]
     * @return: com.itheima.tanhua.vo.mongo.MovementsVoNew
     **/
    public MovementsVoNew findMovementById1(String movementId) {
        return null;
    }
}
