package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.pojo.mongo.Comment;
import com.itheima.tanhua.pojo.mongo.CommentType;

import java.util.List;

public interface CommentServiceApi {

    /**
     * @description: 进行点赞或喜欢操作
     * @author: 黄伟兴
     * @date: 2022/9/26 10:34
     * @param: [movementId, userId, like] 
     * @return: boolean
     **/
    boolean isLikeOrLove(String movementId, Long userId, CommentType like);

    /**
     * @description: 保存评论
     * @author: 黄伟兴
     * @date: 2022/9/26 19:40
     * @param: [comment]
     * @return: java.lang.Integer
     **/
    Integer save(Comment comment);

    /**
     * @description: 分页查询评论列表
     * @author: 黄伟兴
     * @date: 2022/9/26 19:04
     * @param: [movementId, commentType, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Comment>
     **/
    List<Comment> findComments(String movementId, CommentType commentType, Integer page, Integer pagesize);

    /**
     * @description: 删除点赞或者喜欢的数据
     * @author: 黄伟兴
     * @date: 2022/9/26 21:18
     * @param: [comment]
     * @return: java.lang.Integer
     **/
    Integer remove(Comment comment);

}
