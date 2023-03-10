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
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        //??????userID??????????????????
        UserInfo userInfo = userInfoServiceApi.findById(Convert.toLong(userID));

        //?????????UsersInfoVo
        UsersInfoVo usersInfoVo = new UsersInfoVo();
        BeanUtil.copyProperties(userInfo,usersInfoVo);
        //???????????????????????????
        String userStatus = redisTemplate.opsForValue().get(Constants.USER_FREEZE + userID);
        if (StrUtil.equals(userStatus,UserStatus.FREEZE.getType())){
            //???????????????????????????userStatus?????????2
            usersInfoVo.setUserStatus(UserStatus.FREEZE.getType());
        }
        return usersInfoVo;
    }

    public void freeze(FreezeDto freezeDto) {
        //?????????????????????redis???
        String hashKey = Constants.FREEZE_USER+freezeDto.getUserId();
        redisTemplate.opsForHash().put(hashKey,"freezingTime",Convert.toStr(freezeDto.getFreezingTime()));
        redisTemplate.opsForHash().put(hashKey,"freezingRange",Convert.toStr(freezeDto.getFreezingRange()));
        redisTemplate.opsForHash().put(hashKey,"reasonsForFreezing",freezeDto.getReasonsForFreezing());
        redisTemplate.opsForHash().put(hashKey,"frozenRemarks",freezeDto.getFrozenRemarks());
        //??????freezingTime??????????????????
        if (FreezingTime.THREE_DAY.getType() == freezeDto.getFreezingTime()){
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType(),3, TimeUnit.DAYS);
        }else  if (FreezingTime.ONE_WEEK.getType() == freezeDto.getFreezingTime()){
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType(),7, TimeUnit.DAYS);
        }else{
            redisTemplate.opsForValue().set(Constants.USER_FREEZE+freezeDto.getUserId(),UserStatus.FREEZE.getType());
        }
    }

    public void unfreeze(Integer userId, String frozenRemarks) {
        //?????????????????????redis???
        redisTemplate.opsForHash().put(Constants.FREEZE_USER+userId,"frozenRemarks",frozenRemarks);
        //??????redis??????????????????
        redisTemplate.delete(Constants.USER_FREEZE+userId);
    }


    /**
     * @description: ??????-????????????id?????????????????????????????????
     * @author: ?????????
     * @date: 2022/10/11 16:40
     * @param: [page, pagesize, uid, state]
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.mongo.PageResult < com.itheima.tanhua.vo.mongo.MovementsVo>>
     **/
    public PageResult<MovementsVoNew> findMovementByIdAndState(Integer page, Integer pagesize, Long id, Integer state) {

        if(state==null){
            state=0;
        }

        if (id != null) {
            Long counts = movementServiceApi.findAll(id,state);
            //2.?????????????????????????????????  ???mongodb???
            List<Movement> movementList = movementServiceApi.findMovementByIdAndState(id, state, page, pagesize);
            //4.???????????????????????????
            PageResult<MovementsVoNew> pageResult = getPageResultOfVoList1(page, pagesize, counts, movementList);
            return pageResult;
        }else{
            //????????????????????????
            Long counts = movementServiceApi.findAll(state);
            ArrayList<Long> ids = movementServiceApi.findUserIds();
            List<Movement> movementList = movementServiceApi.findMovementByIdAndState1(ids, state, page, pagesize);

            PageResult<MovementsVoNew> pageResult = getPageResultOfVoList1(page, pagesize, counts, movementList);
            return pageResult;
        }


    }

    public PageResult<MovementsVoNew> findMovementByIdAndState(Integer page, Integer pagesize, Long userId, Integer state,String sortProp,String sortOrder) {

        if(state==null){

            //????????????????????????
            Long counts = movementServiceApi.findAll();
            ArrayList<Long> ids = movementServiceApi.findUserIds();
            List<Movement> movementList = movementServiceApi.findMovementByIdAndState1(ids, page, pagesize,sortProp,sortOrder);

            PageResult<MovementsVoNew> pageResult = getPageResultOfVoList1(page, pagesize, counts, movementList);
            return pageResult;
        }

        if (userId != null) {
            Long counts = movementServiceApi.findAll(userId,state);
            //2.?????????????????????????????????  ???mongodb???
            List<Movement> movementList = movementServiceApi.findMovementByIdAndState(userId, state, page, pagesize,sortProp,sortOrder);
            //4.???????????????????????????
            PageResult<MovementsVoNew> pageResult = getPageResultOfVoList1(page, pagesize, counts, movementList);
            return pageResult;
        }else{
            //????????????????????????
            Long counts = movementServiceApi.findAll(state);
            ArrayList<Long> ids = movementServiceApi.findUserIds();
            List<Movement> movementList = movementServiceApi.findMovementByIdAndState1(ids, state, page, pagesize,sortProp,sortOrder);

            PageResult<MovementsVoNew> pageResult = getPageResultOfVoList1(page, pagesize, counts, movementList);
            return pageResult;
        }


    }

    /**
     * @description: ??????MovementVoNew??????
     * @author: ?????????
     * @date: 2022/10/12 9:34
     * @param: [page, pagesize, movementList]
     * @return: com.itheima.tanhua.vo.mongo.PageResult<com.itheima.tanhua.vo.mongo.MovementsVoNew>
     **/
    private PageResult<MovementsVoNew> getPageResultOfVoList1(Integer page, Integer pagesize, Long counts, List<Movement> movementList) {

        ArrayList<MovementsVoNew> list = new ArrayList<>();
        for (Movement movement : movementList) {

            //1.???????????????????????????
            UserInfo userInfo = userInfoServiceApi.findById(movement.getUserId());

            //2.????????????
            MovementsVoNew vo = new MovementsVoNew();
            vo.setId(movement.getId().toHexString());
            vo.setNickname(userInfo.getNickname()==null?"??????":userInfo.getNickname());
            vo.setUserId(movement.getUserId());
            vo.setAvatar(userInfo.getAvatar());
            vo.setCreateDate(Convert.toInt(movement.getCreated()));
            vo.setTextContent(movement.getTextContent());
            vo.setImageContent(movement.getMedias().toArray(new String[0]));
            vo.setState(movement.getState());
            vo.setCommentCount(movement.getCommentCount());
            vo.setLikeCount(movement.getLikeCount());

            list.add(vo);

        }

        return new PageResult<MovementsVoNew>(page, pagesize, counts, list);
    }



    /**
     * @description: ????????????
     * @author: ?????????
     * @date: 2022/10/11 17:22
     * @param: [movementId]
     * @return: java.lang.String
     **/
    public String setMovementTop(String movementId) {

        Boolean flag = movementServiceApi.setMovementTop(movementId);

        return flag ? "?????????????????????" : "??????????????????";
    }

    /**
     * @description: ??????????????????
     * @author: ?????????
     * @date: 2022/10/11 17:30
     * @param: [movementId]
     * @return: java.lang.String
     **/
    public String divestMovementTop(String movementId) {

        Boolean flag = movementServiceApi.divestMovementTop(movementId);

        return flag ? "???????????????????????????" : "????????????????????????";
    }

    /**
     * @description: ????????????
     * @author: ?????????
     * @date: 2022/10/11 17:46
     * @param: [movementId]
     * @return: com.itheima.tanhua.vo.mongo.MovementsVoNew
     **/
    public String approveMovement(String[] movementIds) {
        Boolean flag = movementServiceApi.approveMovement(movementIds);

        return flag ? "????????????" : "???????????????";
    }

    /**
     * @description: ????????????
     * @author: ?????????
     * @date: 2022/10/11 17:57
     * @param: [movementIds]
     * @return: java.lang.String
     **/
    public String rejectMovement(String[] movementIds) {
        Boolean flag = movementServiceApi.rejectMovement(movementIds);

        return flag ? "?????????????????????" : "?????????????????????";
    }

    /**
     * @description: ??????????????????-??????
     * @author: ?????????
     * @date: 2022/10/11 18:09
     * @param: [movementId]
     * @return: com.itheima.tanhua.vo.mongo.MovementsVoNew
     **/
    public MovementsVoNew findMovementById1(String movementId) {
        Movement movement = movementServiceApi.findMovementByMovementId(movementId);

        //1.???????????????????????????
        UserInfo userInfo = userInfoServiceApi.findById(movement.getUserId());

        ///2.????????????
        return MovementsVoNew.init(movement, userInfo);
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
