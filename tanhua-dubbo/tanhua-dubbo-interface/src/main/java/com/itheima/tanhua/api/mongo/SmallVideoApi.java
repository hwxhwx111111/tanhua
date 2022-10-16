package com.itheima.tanhua.api.mongo;



import com.itheima.tanhua.pojo.mongo.CommentVideo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.Video;

import java.util.List;
import java.util.Set;

/**
 * @author 袁鹏
 * @date 2022-09-26-10:15
 */
public interface SmallVideoApi {
    List<Video> page(Long userId, long page, int pagesize);

    Set<Long> hasFocus(Long userId);

    Set<String> hasLike(Long userId);

    String save(Long userId, String coverURL, String videoURL);

    void focus(Long userId, long focusId);

    void unfocus(Long userId, long focusId);

    void like(Long aLong, String videoId);

    void unlike(Long aLong, String videoId);

    //保存视频评论
    void saveComment(CommentVideo commentVideo);

    //根据id查询是否点赞
    CommentVideo findCommentByCid(String cid, Long aLong);

    //判断评论是否被当前操作者点赞
    boolean commentHasLike(Long userId, String id);

    //分页查询评论列表
    PageResult commentsById(String vid, Integer page, Integer pagesize);


    //取消点赞
    void deleteComment(CommentVideo commentVideo);

    //根据推荐的vid查询视频数据
    List<Video> findVideosByIds(List<Long> vids);

    //分页查询视频数据
    List<Video> queryVideos(Integer page, int pagesize);
}
