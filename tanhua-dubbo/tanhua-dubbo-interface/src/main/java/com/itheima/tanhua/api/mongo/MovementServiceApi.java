package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.Movement;
import org.bson.types.ObjectId;

import java.util.ArrayList;
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

    /**
     * @description: 根据动态id查询动态
     * @author: 黄伟兴
     * @date: 2022/10/11 16:45
     * @param: [movementId]
     * @return: com.itheima.tanhua.pojo.mongo.Movement
     **/
    Movement findMovementByMovementId(String movementId);

    /**
     * @description: 后台-根据用户id和动态状态分页查询动态
     * @author: 黄伟兴
     * @date: 2022/10/11 16:45
     * @param: [uid, state, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Movement>
     **/
    List<Movement> findMovementByIdAndState(Long uid, Integer state, Integer page, Integer pagesize);

    List<Movement> findMovementByIdAndState(Long uid, Integer state, Integer page, Integer pagesize ,String sortProp,String sortOrder);

    List<Movement> findMovementByIdAndState1(ArrayList<Long> ids, Integer state, Integer page, Integer pagesize);

    List<Movement> findMovementByIdAndState1(ArrayList<Long> ids, Integer state, Integer page, Integer pagesize,String sortProp,String sortOrder);

    List<Movement> findMovementByIdAndState1(ArrayList<Long> ids, Integer page, Integer pagesize,String sortProp,String sortOrder);


    /**
     * @description: 动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:24
     * @param: [movementId]
     * @return: java.lang.Boolean
     **/
    Boolean setMovementTop(String movementId);

    /**
     * @description: 取消动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:30
     * @param: [movementId]
     * @return: java.lang.String
     **/
    Boolean divestMovementTop(String movementId);

    /**
     * @description: 动态通过
     * @author: 黄伟兴
     * @date: 2022/10/11 17:48
     * @param: [movementId]
     * @return: com.itheima.tanhua.vo.mongo.MovementsVoNew
     **/
    Boolean approveMovement(String[] movementIds);

    /**
     * @description: 动态拒绝
     * @author: 黄伟兴
     * @date: 2022/10/11 17:59
     * @param: [movementIds]
     * @return: java.lang.Boolean
     **/
    Boolean rejectMovement(String[] movementIds);

    Long findAll(Long id,Integer state);
    Long findAll();

    Long findAll( Integer state);

    ArrayList<Long> findUserIds();



}
