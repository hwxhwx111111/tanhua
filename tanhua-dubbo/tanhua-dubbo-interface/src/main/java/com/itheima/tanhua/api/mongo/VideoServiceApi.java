package com.itheima.tanhua.api.mongo;

import com.itheima.tanhua.vo.mongo.Video;

import java.util.List;

public interface VideoServiceApi {

    String save(Video video);

    List<Video> queryVideoList(Integer page, Integer pagesize);

    List<Video> findVideosById(List<Long> vids);

    List<Video> findPageByUserId(Integer page, Integer pagesize, String uid);

    Long count(String uid);
}
