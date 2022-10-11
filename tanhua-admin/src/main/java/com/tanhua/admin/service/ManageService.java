package com.tanhua.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.CommentServiceApi;
import com.itheima.tanhua.api.mongo.MovementServiceApi;
import com.itheima.tanhua.api.mongo.VideoServiceApi;
import com.itheima.tanhua.dto.db.FreezeDto;
import com.itheima.tanhua.enums.FreezingTime;
import com.itheima.tanhua.enums.UserStatus;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Comment;
import com.itheima.tanhua.pojo.mongo.CommentType;
import com.itheima.tanhua.pojo.mongo.Movement;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.db.UsersInfoVo;
import com.itheima.tanhua.vo.mongo.*;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ManageService {
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private MovementServiceApi movementServiceApi;
@DubboReference
private CommentServiceApi commentServiceApi;
@DubboReference
private VideoServiceApi videoServiceApi;

    public PageResult findPageUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> ipage = userInfoServiceApi.findPageUsers(page, pagesize);
        long total = ipage.getTotal();
        List<UsersInfoVo> list = new ArrayList<>();
        for (UserInfo record : ipage.getRecords()) {
            UsersInfoVo vo = new UsersInfoVo();
            BeanUtil.copyProperties(record, vo);
            String userStatus = redisTemplate.opsForValue().get(Constants.USER_FREEZE + record.getId());
            if (StrUtil.equals(userStatus, UserStatus.FREEZE.getType())) {
                vo.setUserStatus(UserStatus.FREEZE.getType());
            }
            list.add(vo);
        }
        PageResult<UsersInfoVo> result = new PageResult<>(page, pagesize, total, list);
        return result;
    }

    public UsersInfoVo findByUserId(String userID) {
        //根据userID查询用户详情
        UserInfo userInfo = userInfoServiceApi.findById(Convert.toLong(userID));

        //封装为UsersInfoVo
        UsersInfoVo usersInfoVo = new UsersInfoVo();
        BeanUtil.copyProperties(userInfo,usersInfoVo);
        //判断是否为冻结状态
        String userStatus = redisTemplate.opsForValue().get(Constants.USER_FREEZE + userID);
        if (StrUtil.equals(userStatus,UserStatus.FREEZE.getType())){
            //如果为冻结状态则将userStatus设置为2
            usersInfoVo.setUserStatus(UserStatus.FREEZE.getType());
        }
        return usersInfoVo;
    }

    public void freeze(FreezeDto freezeDto) {
        //将冻结详情放入redis中
        String hashKey = Constants.FREEZE_USER+freezeDto.getUserId();
        redisTemplate.opsForHash().put(hashKey,"freezingTime",Convert.toStr(freezeDto.getFreezingTime()));
        redisTemplate.opsForHash().put(hashKey,"freezingRange",Convert.toStr(freezeDto.getFreezingRange()));
        redisTemplate.opsForHash().put(hashKey,"reasonsForFreezing",freezeDto.getReasonsForFreezing());
        redisTemplate.opsForHash().put(hashKey,"frozenRemarks",freezeDto.getFrozenRemarks());
        //根据freezingTime设置冻结时间
        if (FreezingTime.THREE_DAY.getType() == freezeDto.getFreezingTime()){
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType(),3, TimeUnit.DAYS);
        }else  if (FreezingTime.ONE_WEEK.getType() == freezeDto.getFreezingTime()){
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType(),7, TimeUnit.DAYS);
        }else{
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType());
        }
    }

    public void unfreeze(Integer userId, String frozenRemarks) {
        //将解封信息存入redis中
        redisTemplate.opsForHash().put(Constants.FREEZE_USER+userId,"frozenRemarks",frozenRemarks);
        //修改redis中的冻结信息
        redisTemplate.delete(Constants.USER_FREEZE+userId);
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

    public PageResult comments(Integer page, Integer pagesize, String messageID) {
        List<Comment> comments = commentServiceApi.findComments(messageID, CommentType.COMMENT, page, pagesize);
        Long counts = commentServiceApi.count(messageID, CommentType.COMMENT.getType());
        List<CommentVoNew> list = new ArrayList();
        for (Comment comment : comments) {

            Long userId = comment.getUserId();
            UserInfo userInfo = userInfoServiceApi.findById(userId);
            CommentVoNew commentVoNew = new CommentVoNew();
            BeanUtil.copyProperties(comment, commentVoNew, new String[0]);
            commentVoNew.setCreateDate(comment.getCreated());
            commentVoNew.setNickname(userInfo.getNickname());
            list.add(commentVoNew);
        }
        PageResult<CommentVoNew> result = new PageResult(page, pagesize, counts, list);
        return result;
    }

    public PageResult videos(Integer page, Integer pagesize, String uid) {
        List<Video> videoList = videoServiceApi.findPageByUserId(page, pagesize, uid);
        Long counts = videoServiceApi.count(uid);
        List<VideoVoNew> list = new ArrayList();
        for (Video video : videoList) {
            VideoVoNew vo = new VideoVoNew();
            BeanUtil.copyProperties(video, vo, new String[]{"id"});
            vo.setId(video.getVid());
            vo.setCreateDate(video.getCreated());
            UserInfo userInfo = userInfoServiceApi.findById(video.getUserId());
            vo.setNickname(userInfo.getNickname());
            list.add(vo);
        }
        PageResult<VideoVoNew> result = new PageResult(page, pagesize, counts, list);
        return result;
    }
}
