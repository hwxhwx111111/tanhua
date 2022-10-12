package com.itheima.tanhua.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.CommentServiceApi;
import com.itheima.tanhua.api.mongo.MovementServiceApi;
import com.itheima.tanhua.exception.BusinessException;
import com.itheima.tanhua.exception.ErrorResult;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Comment;
import com.itheima.tanhua.pojo.mongo.CommentType;
import com.itheima.tanhua.pojo.mongo.Movement;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.mongo.CommentVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommentService {

    @DubboReference
    private CommentServiceApi commentServiceApi;

    @DubboReference
    private MovementServiceApi movementServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * @description: 对动态进行评论
     * @author: 黄伟兴
     * @date: 2022/9/26 18:43
     * @param: [movementId, String]
     * @return: org.springframework.http.ResponseEntity
     **/
    public void saveComments(String movementId, String commentText) {
        //1、获取操作用户id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //2、构造Comment
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setContent(commentText);//写入评论文字
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());

        //3、调用API公共方法  保存评论, 点赞，评论，喜欢加1     并获取评论数量
        Integer commentCount = commentServiceApi.save(comment);
        log.info("commentCount", commentCount);

    }

    /**
     * @description: 评论列表分页查询
     * @author: 黄伟兴
     * @date: 2022/9/26 9:12
     * @param: [page, pagesize, movementId]   根据动态id查询评论列表
     * @return: com.itheima.tanhua.vo.mongo.PageResult<com.itheima.tanhua.pojo.mongo.Comment>
     **/
    public PageResult<CommentVo> findComments(Integer page, Integer pagesize, String movementId) {

        //1、调用API查询评论列表
        List<Comment> commentList = commentServiceApi.findComments(movementId, CommentType.COMMENT, page, pagesize);

        //2、判断list集合是否存在
        if (CollUtil.isEmpty(commentList)) {
            return new PageResult<CommentVo>();
        }

        //3、提取所有的用户id,调用UserInfoAPI查询用户详情
        List<Long> userIds = CollUtil.getFieldValues(commentList, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoServiceApi.findByIds(userIds, null);

        //4、构造vo对象
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            UserInfo userInfo = userInfoMap.get(comment.getUserId());
            if (userInfo != null) {
                //调用静态方法，组装信息
                CommentVo commentVo = CommentVo.init(userInfo, comment);
                commentVoList.add(commentVo);
            }
        }

        //5、构造返回值
        return new PageResult<CommentVo>(page, pagesize, 0L,commentVoList);

    }

    /**
     * @description: 点赞
     * @author: 黄伟兴
     * @date: 2022/9/26 10:28
     * @param: [movementId]
     * @return: java.lang.Integer
     **/
    public Integer like(String movementId) {
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1.调用API查询用户是否点赞
        boolean hasComment = commentServiceApi.isLikeOrLove(movementId, userId, CommentType.LIKE);

        //2.如果已经点赞，抛出异常
        if (hasComment) {
           // throw new ConsumerException("点赞失败！");
            throw new BusinessException(ErrorResult.likeError());
        }

        //3.调用API保存数据到mongodb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentServiceApi.save(comment);

        //4.拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        redisTemplate.opsForHash().put(key, hashKey, String.valueOf(1));


        //TODO 待补充
        Map<String,String> map = new HashMap<>();
        map.put("userId",userId.toString());
        map.put("busId",movementId);
        map.put("type","0201");
        map.put("date",System.currentTimeMillis()+"");
        //向MQ发送消息
        //rabbitTemplate.ConvertAndSend();


        //返回点赞数
        return count;
    }

    /**
     * @description: 取消点赞，根据动态id
     * @author: 黄伟兴
     * @date: 2022/9/26 20:40
     * @param: [movementId] 动态id
     * @return: org.springframework.http.ResponseEntity
     **/
    public Integer disLike(String movementId) {
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1.调用API查询用户是否点赞
        boolean hasComment = commentServiceApi.isLikeOrLove(movementId, userId, CommentType.LIKE);

        //2.如果未点赞，抛出异常
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //3.已经点赞，删除数据，返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(userId);
        Integer count = commentServiceApi.remove(comment);

        //4.拼接redis的key，将redis中用户的点赞状态删除
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        redisTemplate.opsForHash().delete(key, hashKey);


        Map<String,String> map = new HashMap<>();
        map.put("userId",userId.toString());
        map.put("busId",movementId);
        map.put("type","0206");
        map.put("date",System.currentTimeMillis()+"");
        //向MQ发送消息



        //返回点赞数
        return count;
    }

    /**
     * @description: 对动态进行喜欢，根据动态的id
     * @author: 黄伟兴
     * @date: 2022/9/26 10:27
     * @param: [movementId]   动态的id
     * @return: org.springframework.http.ResponseEntity
     **/
    public Integer love(String movementId) {
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1.调用API查询用户是否喜欢
        boolean hasComment = commentServiceApi.isLikeOrLove(movementId, userId, CommentType.LOVE);

        //2.如果已经喜欢，抛出异常
        if (hasComment) {
           // throw new ConsumerException("喜欢失败！");
            throw new BusinessException(ErrorResult.loveError());
        }

        //3.调用API保存数据到mongodb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer count = commentServiceApi.save(comment);

        //4.拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        redisTemplate.opsForHash().put(key, hashKey, String.valueOf(1));

        //返回点赞数
        return count;


    }

    /**
     * @description: 取消喜欢，根据动态id
     * @author: 黄伟兴
     * @date: 2022/9/26 20:40
     * @param: [movementId] 动态id
     * @return: org.springframework.http.ResponseEntity
     **/
    public Integer unLove(String movementId) {
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //1.调用API查询用户是否喜欢
        boolean hasComment = commentServiceApi.isLikeOrLove(movementId, userId, CommentType.LOVE);

        //2.如果未喜欢，抛出异常
        if (!hasComment) {
           // throw new ConsumerException("点赞喜欢失败！");
            throw new BusinessException(ErrorResult.disloveError());
        }
        //3.已经喜欢，删除数据，返回喜欢数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(userId);
        Integer count = commentServiceApi.remove(comment);

        //4.拼接redis的key，将redis中用户的点赞状态删除
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        redisTemplate.opsForHash().delete(key, hashKey);

        //返回喜欢数
        return count;
    }

    /**
     * 取消点赞
     * @param id
     * @return
     */
    public Integer dislike(String id) {
        List<Long>pids=new ArrayList<>();
        pids.add(Convert.toLong(id));
        List<Movement> movement = movementServiceApi.findMovementByPids(pids);
        Integer count=0;
        for (Movement movement1 : movement) {
            ObjectId movementId = movement1.getId();
            count=disLike(Convert.toStr(movementId));
        }
        return count;
    }

    public List<Comment> findById(Long userId) {
        List<Comment> commentList = commentServiceApi.findById(userId,CommentType.LIKE);
        return commentList;
    }

    public List<Comment> findByIdLike(Long userId) {
        List<Comment> commentList = commentServiceApi.findById(userId,CommentType.COMMENT);
        return commentList;
    }
}
