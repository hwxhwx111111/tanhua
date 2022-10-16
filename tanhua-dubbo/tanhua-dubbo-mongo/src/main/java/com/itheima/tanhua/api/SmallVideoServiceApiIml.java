package com.itheima.tanhua.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.api.mongo.SmallVideoApi;
import com.itheima.tanhua.pojo.mongo.CommentVideo;
import com.itheima.tanhua.pojo.mongo.FocusUser;
import com.itheima.tanhua.pojo.mongo.SmallVideoComment;
import com.itheima.tanhua.utils.IdWorker;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 袁鹏
 * @date 2022-09-26-10:16
 */
@DubboService
public class SmallVideoServiceApiIml implements SmallVideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public List<Video> page(Long userId, long page, int pagesize) {
        return mongoTemplate.find(new Query().skip((page - 1) * pagesize).limit(pagesize), Video.class);
    }

    @Override
    public Set<Long> hasFocus(Long userId) {
        List<FocusUser> focusUserList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), FocusUser.class);
        return new HashSet<>(CollUtil.getFieldValues(focusUserList, "followUserId", Long.class));
    }

    @Override
    public Set<String> hasLike(Long userId) {
        // todo 没有对应表
        List<SmallVideoComment> smallVideoComments = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId).and("commentType").is(0)), SmallVideoComment.class);
        return new HashSet<>(CollUtil.getFieldValues(smallVideoComments, "videoId", String.class));
    }

    @Override
    public String save(Long userId, String coverURL, String videoURL) {
        Video video = new Video();
        video.setVideoUrl(videoURL);
        video.setVid(idWorker.getNextId("video"));
        video.setCommentCount(0);
        video.setCreated(System.currentTimeMillis());
        video.setLikeCount(0);
        video.setLoveCount(0);
        video.setPicUrl(coverURL);
        video.setText("text");
        video.setUserId(userId);
        Video save = mongoTemplate.save(video);
        return Convert.toStr(save.getId());
    }

    @Override
    public void focus(Long userId, long focusId) {
        if(!hasFocus(userId, focusId)){
            FocusUser focusUser = new FocusUser();
            focusUser.setUserId(userId);
            focusUser.setFollowUserId(focusId);
            focusUser.setCreated(System.currentTimeMillis());
            mongoTemplate.save(focusUser);
        }
    }

    @Override
    public void unfocus(Long userId, long focusId) {
        if(hasFocus(userId, focusId)){
            Criteria criteria = Criteria.where("userId").is(userId).and("followUserId").is(focusId);
            mongoTemplate.remove(Query.query(criteria), FocusUser.class);
        }
    }

    @Override
    public void like(Long userId, String videoId) {
        if(!hasLike(userId, videoId)) {
            SmallVideoComment comment = new SmallVideoComment();
            comment.setCommentType(0);
            comment.setVideoId(videoId);
            comment.setUserId(userId);
            mongoTemplate.save(comment);
            Update update = new Update();
            update.inc("likeCount");
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(videoId)), update, Video.class);
        }
    }

    @Override
    public void unlike(Long userId, String videoId) {
        if(hasLike(userId, videoId)){
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId).and("videoId").is(videoId).and("commentType").is(0)), SmallVideoComment.class);
            Update update = new Update();
            update.inc("likeCount", -1);
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(videoId)), update, Video.class);
        }
    }



    /**
     * 保存视频评论
     * @param commentVideo
     */
    public void saveComment(CommentVideo commentVideo) {
        if (commentVideo.getCommentType()==2){//说明是评论,需要设置对方的userId
            //构造查询条件
            Query query = Query.query(Criteria.where("id").is(commentVideo.getVideoId()));
            Video video = mongoTemplate.findOne(query, Video.class);
            //补充评论属性
            commentVideo.setVideoUserId(video.getUserId());
        }else {//说明是点赞,需要更新数据
            //点赞就需要更新likecount字段
            //构造query条件
            Query commentQuery = Query.query(Criteria.where("id").is(commentVideo.getCid())
                    .and("commentType").is(2));
            Update update = new Update();
            update.inc("likeCount", 1);
            //更新数据
            mongoTemplate.updateFirst(commentQuery,update,CommentVideo.class);
        }
        commentVideo.setCreated(System.currentTimeMillis());
        //保存点赞数据到mongo
        mongoTemplate.save(commentVideo);

    }

    /**
     * 根据cid和userid查询评论数据
     * @param cid
     * @param userId
     * @return
     */
    public CommentVideo findCommentByCid(String cid, Long userId) {
        Query query = Query.query(Criteria.where("cid").is(cid)
                .and("userId").is(userId)
                .and("commentType").is(1));
        return mongoTemplate.findOne(query,CommentVideo.class);
    }

    /**
     * 评论是否点赞
     * @param userId
     * @param cid
     * @return
     */
    public boolean commentHasLike(Long userId, String cid){
        return mongoTemplate.exists(Query.query(Criteria.where("userId").is(userId)
                .and("cid").is(cid).and("commentType").is(1)), CommentVideo.class);
    }

    /**
     * 分页查询评论列表
     * @param vid
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult commentsById(String vid, Integer page, Integer pagesize) {
        //构造查询条件,查询vid对应的所有评论(type为2)
        Query query = Query.query(Criteria.where("videoId").is(vid)
                .and("commentType").is(2));
        long count = mongoTemplate.count(query, CommentVideo.class);
        //分页条件和排序
        query.limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<CommentVideo> commentVideos = mongoTemplate.find(query, CommentVideo.class);
        PageResult result = new PageResult(page, pagesize, commentVideos);
        result.setCounts(count);
        return result;
    }

    /**
     * 取消点赞
     * @param commentVideo
     */
    public void deleteComment(CommentVideo commentVideo) {
        //构造查询条件,查询vid对应的所有评论(type为2)
        Query query = Query.query(Criteria.where("cid").is(commentVideo.getCid())
                .and("userId").is(commentVideo.getUserId())
                .and("commentType").is(1));
        mongoTemplate.remove(query,CommentVideo.class);
        //取消点赞也需要更新likecount字段
        //构造query条件
        Query commentQuery = Query.query(Criteria.where("id").is(commentVideo.getCid())
                .and("commentType").is(2));
        Update update = new Update();
        update.inc("likeCount", -1);
        //更新数据
        mongoTemplate.updateFirst(commentQuery,update,CommentVideo.class);
    }

    /**
     * 根据推荐的vid查询视频数据
     * @param vids
     * @return
     */
    public List<Video> findVideosByIds(List<Long> vids) {
        Query query = Query.query(Criteria.where("vid").in(vids));
        return mongoTemplate.find(query,Video.class);
    }

    /**
     * 分页查询视频数据
     * @param page
     * @param pagesize
     * @return
     */
    public List<Video> queryVideos(Integer page, int pagesize) {
        Query query = new Query().limit(pagesize).skip((page-1)*pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query,Video.class);
    }


    private boolean hasFocus(Long userId, Long focusId){
        Criteria criteria = Criteria.where("userId").is(userId).and("followUserId").is(focusId);
        return mongoTemplate.exists(Query.query(criteria), FocusUser.class);
    }

    private boolean hasLike(Long userId, String videoId){
        return mongoTemplate.exists(Query.query(Criteria.where("userId").is(userId).and("videoId").is(videoId).and("commentType").is(0)), SmallVideoComment.class);
    }
}
