package com.itheima.tanhua.web;


import com.itheima.tanhua.service.SmallVideoService;
import com.itheima.tanhua.service.SmallVideosService;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.VideoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService smallVideosService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SmallVideoService smallVideoService;
    /**
     * @description: 发布视频
     * @author: 黄伟兴
     * @date: 2022/10/5 20:33
     * @param: [videoThumbnail, videoFile]
     * @return: org.springframework.http.ResponseEntity
     **/
  /*  @PostMapping
    public ResponseEntity<String> saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        smallVideosService.saveVideos(videoThumbnail,videoFile);
        return ResponseEntity.ok("视频发布成功");
    }

    *//**
     * @description: 分页查询视频列表
     * @author: 黄伟兴
     * @date: 2022/10/5 21:03
     * @param: [page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **//*
    @GetMapping
    public ResponseEntity findVideoList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize){
        String currentUserId = redisTemplate.opsForValue().get("AUTH_USER_ID");

       PageResult result =  smallVideosService.findVideoList(page,pagesize,currentUserId);
       return ResponseEntity.ok(result);
    }

    *//**
     * @description: 视频点赞
     * @author: 黄伟兴
     * @date: 2022/9/30 14:13
     * @param: [id]  视频id
     * @return: org.springframework.http.ResponseEntity
     **//*
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable Long id){


        smallVideosService.like(id);
        return null;
    }*/


    @GetMapping
    public ResponseEntity<PageResult<VideoVo>> videoPage(@RequestParam(defaultValue = "1")Integer page, @RequestParam(defaultValue = "10") Integer pagesize){
        PageResult<VideoVo> result = smallVideoService.videoPage(page, pagesize);
        return ResponseEntity.ok().body(result);
    }


    @PostMapping
    public ResponseEntity<?> upload(MultipartFile videoThumbnail, MultipartFile videoFile){
        smallVideoService.upload(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/userFocus")
    public ResponseEntity<?> focus(@PathVariable("id") long id){
        smallVideoService.focus(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/userUnFocus")
    public ResponseEntity<?> unfocus(@PathVariable("id") long id){
        smallVideoService.unfocus(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@PathVariable("id") String videoId){
        smallVideoService.like(videoId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<?> unlike(@PathVariable("id") String videoId){
        smallVideoService.unlike(videoId);
        return ResponseEntity.ok(null);
    }


    /**
     * 评论列表
     * @param vid
     * @return
     */
    @GetMapping("{id}/comments")
    public ResponseEntity commentsById(@PathVariable("id") String vid,Integer page, Integer pagesize){

        PageResult pageResult = smallVideoService.commentsById(vid,page,pagesize);
        return ResponseEntity.ok(pageResult);

    }

    /**
     * 发布评论
     * @param
     * @return
     */
    @PostMapping("{id}/comments")
    public ResponseEntity saveComment(@RequestBody Map<String,String> parm){
        String vid = parm.get("id");
        String comment = parm.get("comment");

        smallVideoService.saveComment(vid,comment);
        return ResponseEntity.ok(null);

    }
    /**
     * 评论点赞
     * @param cid
     * @return
     */
    @PostMapping("{id}/comments/like")
    public ResponseEntity likeComment(@PathVariable("id") String cid){

        smallVideoService.likeComment(cid);
        return ResponseEntity.ok(null);

    }
    /**
     * 取消点赞
     * @param cid
     * @return
     */
    @PostMapping("{id}/comments/dislike")
    public ResponseEntity dislikeComment(@PathVariable("id") String cid){

        smallVideoService.dislikeComment(cid);
        return ResponseEntity.ok(null);

    }
}
