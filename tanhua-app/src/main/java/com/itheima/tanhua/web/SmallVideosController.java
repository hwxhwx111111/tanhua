package com.itheima.tanhua.web;


import com.itheima.tanhua.service.SmallVideosService;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService smallVideosService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @description: 发布视频
     * @author: 黄伟兴
     * @date: 2022/10/5 20:33
     * @param: [videoThumbnail, videoFile]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping
    public ResponseEntity<String> saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        smallVideosService.saveVideos(videoThumbnail,videoFile);
        return ResponseEntity.ok("视频发布成功");
    }

    /**
     * @description: 分页查询视频列表
     * @author: 黄伟兴
     * @date: 2022/10/5 21:03
     * @param: [page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping
    public ResponseEntity findVideoList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize){
        String currentUserId = redisTemplate.opsForValue().get("AUTH_USER_ID");

       PageResult result =  smallVideosService.findVideoList(page,pagesize,currentUserId);
       return ResponseEntity.ok(result);
    }

    /**
     * @description: 视频点赞
     * @author: 黄伟兴
     * @date: 2022/9/30 14:13
     * @param: [id]  视频id
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable Long id){


        smallVideosService.like(id);
        return null;
    }

}
