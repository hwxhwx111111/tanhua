package com.itheima.tanhua.api;

import com.itheima.tanhua.api.mongo.VideoServiceApi;
import com.itheima.tanhua.utils.IdWorker;
import com.itheima.tanhua.vo.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VideoServiceApiImpl implements VideoServiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;


    /**
     * @description: 保存视频
     * @author: 黄伟兴
     * @date: 2022/10/5 20:51
     * @param: [video]
     * @return: java.lang.String
     **/
    @Override
    public String save(Video video) {

        //1.设置属性
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());

        //2.调用方法保存对象
       video = mongoTemplate.save(video);

        //3.返回对象id
        return video.getId().toHexString();
    }

    /**
     * @description: 分页查询视频列表
     * @author: 黄伟兴
     * @date: 2022/10/5 21:24
     * @param: [i, pagesize]
     * @return: java.util.List<com.itheima.tanhua.vo.mongo.Video>
     **/
    @Override
    public List<Video> queryVideoList(Integer page, Integer pagesize) {

        Query query = new Query().skip((page-1)*pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));

        return mongoTemplate.find(query,Video.class);


    }

    /**
     * @description: 根据vid查询视频
     * @author: 黄伟兴
     * @date: 2022/10/5 21:25
     * @param: [vids]
     * @return: java.util.List<com.itheima.tanhua.vo.mongo.Video>
     **/
    @Override
    public List<Video> findVideosById(List<Long> vids) {
        Query query = Query.query(Criteria.where("vid").in(vids));
        return mongoTemplate.find(query,Video.class);
    }
}
