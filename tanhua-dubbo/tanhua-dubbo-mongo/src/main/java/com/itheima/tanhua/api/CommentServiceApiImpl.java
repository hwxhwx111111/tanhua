package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.CommentServiceApi;
import com.itheima.tanhua.pojo.mongo.Comment;
import com.itheima.tanhua.pojo.mongo.CommentType;
import com.itheima.tanhua.pojo.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


@DubboService
public class CommentServiceApiImpl implements CommentServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * @description: 是否点赞
     * @author: 黄伟兴
     * @date: 2022/9/26 10:34
     * @param: [movementId, userId, like]
     * @return: boolean
     **/
    @Override
    public boolean isLikeOrLove(String movementId, Long userId, CommentType commentType) {

        Query query = Query.query(Criteria
                .where("userId").is(userId)                        //评论者id
                .and("publishId").is(new ObjectId(movementId))    //动态id
                .and("commentType").is(commentType.getType()));   //评论类型
        return mongoTemplate.exists(query, Comment.class);
    }

    /**
     * @description: 保存评论
     * @author: 黄伟兴
     * @date: 2022/9/26 19:28
     * @param: [comment]
     * @return: java.lang.Integer
     **/
    @Override
    public Integer save(Comment comment) {

        //1、查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        //2、向comment对象设置被评论人属性
        if (movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        //3、保存到数据库
        mongoTemplate.save(comment);

        //4、更新动态表中的对应字段 点赞，评论，喜欢
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        } else {
            update.inc("loveCount", 1);
        }

        //设置更新参数 点赞，评论，喜欢  加1
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Movement movement1 = mongoTemplate.findAndModify(query, update, options, Movement.class);

        //5、根据评论类型，获取对应的互动数量，并返回
        return movement1.statisCount(comment.getCommentType());
    }

    /**
     * @description: 分页查询评论列表
     * @author: 黄伟兴
     * @date: 2022/9/26 19:04
     * @param: [movementId, commentType, page, pagesize]
     * @return: java.util.List<com.itheima.tanhua.pojo.mongo.Comment>
     **/
    @Override
    public List<Comment> findComments(String movementId, CommentType commentType, Integer page, Integer pagesize) {
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(commentType.getType()))
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));

        return mongoTemplate.find(query, Comment.class);
    }

    /**
     * @description: 删除点赞或者喜欢的数据
     * @author: 黄伟兴
     * @date: 2022/9/26 21:18
     * @param: [comment]
     * @return: java.lang.Integer
     **/
    @Override
    public Integer remove(Comment comment) {

        //1.删除coment表中的数据
        Query query = Query.query(Criteria
                .where("userId").is(comment.getUserId())                        //评论者id
                .and("publishId").is(comment.getPublishId())    //动态id
                .and("commentType").is(comment.getCommentType()));   //评论类型);
        mongoTemplate.remove(query, Comment.class);

        //2.修改动态表中的点赞数量
        Query movementQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else {
            update.inc("loveCount", -1);
        }

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);

        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public Long count(String messageID, int type) {
        long count = this.mongoTemplate.count(Query.query(Criteria.where("publishId").is(messageID).and("content").is(type)), Comment.class);
        return count;
    }



    @Override
    public List<Comment> findById(Long userId, CommentType like) {
        Query query= Query.query(Criteria.where("userId").is(userId).and("commentType").is(like.getType()));
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        return commentList;
    }
}
