package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.SmallVideoApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.CommentVideo;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.mongo.CommentVideoVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.Video;
import com.itheima.tanhua.vo.mongo.VideoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.itheima.tanhua.utils.Constants.*;


/**
 * @author 袁鹏
 * @date 2022-09-26-10:14
 */
@Service
public class SmallVideoService {

    @DubboReference
    private SmallVideoApi smallVideoApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer webServer;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MqMessageService mqMessageService;

    /**
     * 分页查询视频列表
     * 优先查询推荐数据(从redis中获取vid查询数据库)
     * 如果数据不存在/不满足分页则查询数据库
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult videoPage(Integer page, Integer pagesize) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        //1.优先查询推荐数据(从Redis中获取)
        //1.1拼接对应的key
        String redisKey = VIDEOS_RECOMMEND +redisTemplate.opsForValue().get(Constants.USER_ID);
        String value = redisTemplate.opsForValue().get(redisKey);
        //创建一个存数据的容器
        List<Video> list = new ArrayList<>();
        //定义一个查询总页数的变量
        int redisPage=0;
        //2.判断数据是否存在
        if (!StringUtils.isEmpty(value)){//不为空
            //处理redis中的vid
            String[] values = value.split(",");
            //判断是否足够分页
            if ((page-1)*pagesize<values.length){
                //转换id类型
                List<Long> vids = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize)
                        .map(e -> Long.valueOf(e)).collect(Collectors.toList());
                //调用api查询数据库
                list=smallVideoApi.findVideosByIds(vids);
            }
            //使用工具类计算需要分多少页
            redisPage= PageUtil.totalPage(values.length,pagesize);
        }
        //4.如果redis中数据不存在/不足分页
        if (list.isEmpty()){//说明上面没有进行查询
            //则分页查询数据库
            list=smallVideoApi.queryVideos(page-redisPage,pagesize);
        }
        //5.提取用户id,组装vo对象
        List<Long> userId = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoServiceApi.getUserInfoMap(userId, null);
        List<VideoVo> vos = list.stream().map(item -> {
            //创建vo容器
            VideoVo vo = new VideoVo();
            UserInfo userInfo = map.get(item.getUserId());
            if (userInfo != null) {
                //判断是否喜欢是否点赞(查询操作者关注/点赞的用户id集合)
                Set<Long> hasFocus = smallVideoApi.hasFocus(uid);
                Set<String> hasLike = smallVideoApi.hasLike(uid);
                //判断是否包含了该条数据对应的用户id
                vo = VideoVo.init(userInfo, item, hasFocus.contains(item.getUserId())
                        , hasLike.contains(item.getUserId()));
            }
            return vo;
        }).collect(Collectors.toList());
        //返回数据
        return new PageResult(page,pagesize,vos);
       /* List<Video> videoList = smallVideoApi.page(uid, page, pagesize);
        if(videoList.size() != 0) {
            List<Long> videoUserIdList = CollUtil.getFieldValues(videoList, "userId", Long.class);
            Map<Long, UserInfo> userInfoMap = userInfoServiceApi.getUserInfoMap(videoUserIdList, null);
            Set<Long> hasFocusId = smallVideoApi.hasFocus(uid);
            Set<String> hasLikeId = smallVideoApi.hasLike(uid);
            List<VideoVo> videoVoList = videoList.stream().map(v -> VideoVo.init(userInfoMap.get(v.getUserId()), v, hasFocusId.contains(v.getUserId()), hasLikeId.contains(v.getId()))).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
            PageResult<VideoVo> pageInfo = new PageResult<>();
            pageInfo.setPage(page);
            pageInfo.setPagesize((long) pagesize);
            pageInfo.setItems(videoVoList);
            return pageInfo;
        }else{
            return new PageResult<>();
        }*/
    }

    public void upload(MultipartFile videoThumbnail, MultipartFile videoFile){
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        String url;
        try {
            url = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
            StorePath storePath = storageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), videoFile.getOriginalFilename().substring(videoFile.getOriginalFilename().lastIndexOf(".") + 1), null);
            String webServerUrl = webServer.getWebServerUrl();
            String id = smallVideoApi.save(uid, url, webServerUrl + storePath.getFullPath());

            mqMessageService.sendLogMessage(uid, "0301", "video", id);
        }catch (IOException e) {
            throw new ConsumerException("封面上传失败");
        }

    }

    public void focus(long id) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        smallVideoApi.focus(uid, id);
        // todo 这只是一个猜想
        String key = String.join("_", FOCUS_USER, Convert.toStr(uid), String.valueOf(id));
        redisTemplate.opsForValue().set(key, "1");
    }

    public void unfocus(long id) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        smallVideoApi.unfocus(uid, id);
        String key = String.join("_", FOCUS_USER, Convert.toStr(uid), String.valueOf(id));
        redisTemplate.delete(key);
    }

    public void like(String videoId) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        smallVideoApi.like(uid, videoId);
        // todo 猜想
        String key = VIDEO_INTERACT_KEY + uid;
        String hashKey = VIDEO_LIKE_HASHKEY + videoId;
        redisTemplate.opsForHash().put(key, hashKey, "1");
        //视频点赞,发送日志消息
        mqMessageService.sendLogMessage(uid, "0302", "video", videoId);
    }

    public void unlike(String videoId) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        smallVideoApi.unlike(uid, videoId);
        // todo 猜想
        String key = VIDEO_INTERACT_KEY + uid;
        String hashKey = VIDEO_LIKE_HASHKEY + videoId;
        redisTemplate.opsForHash().delete(key, hashKey);
        //视频取消点赞,发送日志消息
        mqMessageService.sendLogMessage(uid, "0303", "video", videoId);
    }

    /**
     * 评论列表
     * @param vid
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult commentsById(String vid, Integer page, Integer pagesize) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        //查询vid对应的所有评论
       PageResult result= smallVideoApi.commentsById(vid,page,pagesize);
        List<CommentVideo> list = result.getItems();
        if (CollUtil.isEmpty(list)){
            return result;
        }
        //获取评论人id
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //调用api查询对应的userinfo
        Map<Long, UserInfo> map = userInfoServiceApi.getUserInfoMap(userIds, null);
        //组装vo
        List<CommentVideoVo> vos = list.stream().map(item -> {
            CommentVideoVo vo = new CommentVideoVo();
            UserInfo userInfo = map.get(item.getUserId());
            if (userInfo != null) {
                //判断该评论是否被操作者点赞
                if (smallVideoApi.commentHasLike(uid,item.getId())){
                    vo = CommentVideoVo.init(userInfo, item,true);//点赞传个true
                }
                vo = CommentVideoVo.init(userInfo, item,false);//未点赞传false
            }
            return vo;
        }).collect(Collectors.toList());
        result.setItems(vos);
        return result;
    }

    /**
     * 发布视频评论
     * @param videoId
     * @param content
     */
    public void saveComment(String videoId, String content) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        //构造commentvideo对象调用api保存
        CommentVideo commentVideo = new CommentVideo();
        commentVideo.setVideoId(videoId);
        commentVideo.setContent(content);
        commentVideo.setCommentType(2);//评论类型，1-点赞，2-评论
        commentVideo.setUserId(uid);
        smallVideoApi.saveComment(commentVideo);
        //视频评论发布后需要发送日志消息
        mqMessageService.sendLogMessage(uid, "0304", "video", videoId);
    }

    /**
     * 评论点赞/回复(如果是回复还需要传一个回复内容)
     * @param cid
     */
    public void likeComment(String cid) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        //通过评论id和类型查询操作者是否点赞
        CommentVideo commentVideo= smallVideoApi.findCommentByCid(cid,uid);
        if (commentVideo==null){//说明没有点赞
            //构造commentvideo对象调用api保存
            CommentVideo likeCommentVideo = new CommentVideo();
            //likeCommentVideo.setVideoId(commentVideo.getVideoId());
            likeCommentVideo.setCommentType(1);//评论类型，1-点赞，2-评论/回复
            likeCommentVideo.setUserId(uid);//点赞人的id
            likeCommentVideo.setCid(cid);//被点赞的评论id
            smallVideoApi.saveComment(likeCommentVideo);
        }else {
            throw new ConsumerException("已点赞");
        }

    }

    /**
     * 取消点赞
     * @param cid
     */
    public void dislikeComment(String cid) {
        Long uid = Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID));
        //通过评论id和类型查询操作者是否点赞
        CommentVideo commentVideo= smallVideoApi.findCommentByCid(cid, uid);
        if (commentVideo!=null){//说明已经点赞
            //调用api删除
            smallVideoApi.deleteComment(commentVideo);
        }else {
            throw new ConsumerException("已取消点赞");
        }

    }
}
