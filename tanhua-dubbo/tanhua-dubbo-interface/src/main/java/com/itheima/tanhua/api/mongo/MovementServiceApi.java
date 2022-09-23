package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.Movement;
import org.bson.types.ObjectId;

import java.util.List;

public interface MovementServiceApi {

    /**
     * @description: 发布动态
     * @author: 黄伟兴
     * @date: 2022/9/25 14:58
     * @param: [movement]
     * @return: org.bson.types.ObjectId
     **/
    ObjectId publish(Movement movement);

    /**
     * @description: 根据用户id查询其动态
     * @author: 黄伟兴
     * @date: 2022/9/25 16:05
     * @param: [userId, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    List<Movement> findMovementByUserId(Long userId, Integer page, Integer pagesize);


    /**
     * @description: 查询好友动态
     * @author: 黄伟兴
     * @date: 2022/9/24 11:01
     * @param: [page, pagesize]
     * @return: com.itheima.tanhua.vo.mongo.PageResult<com.itheima.tanhua.vo.mongo.MovementsVo>
     **/
    List<Movement> findFriendMovements(Integer page, Integer pagesize,List<Object> movementIds);

    /**
     * @description: 根据pid查询动态数据
     * @author: 黄伟兴
     * @date: 2022/9/25 20:06
     * @param: [pids]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    List<Movement> findMovementByPids(List<Long> pids);

    /**
     * @description: 调用API随机构造10条动态数据
     * @author: 黄伟兴
     * @date: 2022/9/25 20:06
     * @param: [pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    List<Movement> randomMovements(Integer pagesize);

    Movement findMovementByMovementId(String movementId);
}
