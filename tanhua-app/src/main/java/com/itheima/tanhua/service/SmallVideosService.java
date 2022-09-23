package com.itheima.tanhua.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.VideoServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.Video;
import com.itheima.tanhua.vo.mongo.VideoVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    private VideoServiceApi videoServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;


    /**
     * @description: 视频点赞
     * @author: 黄伟兴
     * @date: 2022/9/30 14:16
     * @param: [id] 视频id
     * @return: void
     **/
    public void like(Long id) {
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));


    }

    /**
     * @description: 发布视频
     * @author: 黄伟兴
     * @date: 2022/10/5 20:33
     * @param: [videoThumbnail, videoFile]
     * @return: void
     **/
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {

        //当前登录者id
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_user_Id"));


        if (videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            throw new ConsumerException("视频上传失败");
        }

        //1.将视频上传到FastDFS,获取访问URL
        String filename = videoFile.getOriginalFilename();
        filename = filename.substring(filename.lastIndexOf(".") + 1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl = webServer.getWebServerUrl() + storePath;

        //2.将封面图片上传到阿里云OSS，获取访问URL
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        //3.构建Videos对象
        Video video = new Video();
        video.setUserId(currentUserId);
        video.setPicUrl(imageUrl);
        video.setVideoUrl(videoUrl);
        video.setText("发布视频测试文本");

        //4.调用API保存数据
        String videoId = videoServiceApi.save(video);
        if (StringUtils.isEmpty(videoId)) {
            throw new ConsumerException("视频保存失败");
        }

    }


    /**
     * @description: 分页查询视频列表
     * @author: 黄伟兴
     * @date: 2022/10/5 21:04
     * @param: [page, pagesize]
     * @return: com.itheima.tanhua.vo.mongo.PageResult
     **/
    @Cacheable(value = "videos",key =" #currentUserId+ '_'+  #page+ '_' + #pagesize " )
    public PageResult findVideoList(Integer page, Integer pagesize,String currentUserId) {



        //1.查询redis数据
        String redisKey = Constants.VIDEOS_RECOMMEND + currentUserId;
        String redisValue = redisTemplate.opsForValue().get(redisKey);

        //2.判断redis中的数据是否存在，判断redis中数据是否满足本次分页条数
        int redisPages = 0;
        List<Video> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(redisValue)) {
            //3.如果redis数据存在，根据vid查询数据
            String[] values = redisValue.split(",");
            //判断当前页的起始条数是否小于数组总数
            List<Long> vids = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize).map(e -> Long.valueOf(e)).collect(Collectors.toList());
            //5.调用Api根据pid数组查询动态数据
            list = videoServiceApi.findVideosById(vids);
            redisPages = PageUtil.totalPage(values.length, pagesize);
        }

        //4.如果redis数据不存在，分页查询视频列表
        list = videoServiceApi.queryVideoList(page - redisPages, pagesize);

        //5.提取视频列表中所有的用户id
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);

        //6.查询用户信息
        Map<Long, UserInfo> userInfoMap = userInfoServiceApi.findByIds(userIds, null);

        //7.构建返回值
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = userInfoMap.get(video.getUserId());
            if (userInfo != null) {
                VideoVo vo = VideoVo.init(userInfo, video);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0L, vos);

    }
}
