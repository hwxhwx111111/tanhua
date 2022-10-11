package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.MovementServiceApi;
import com.itheima.tanhua.api.mongo.VisitorsServiceApi;
import com.itheima.tanhua.dto.mongo.MovementDto;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.Movement;
import com.itheima.tanhua.pojo.mongo.Visitors;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.utils.RelativeDateFormat;
import com.itheima.tanhua.vo.mongo.MovementsVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.VisitorsVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovementService {

    @DubboReference
    private MovementServiceApi movementServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private TimeLineService timeLineService;

    @DubboReference
    private VisitorsServiceApi visitorsServiceApi;


    /**
     * @description: 发布动态
     * @author: 黄伟兴
     * @date: 2022/9/24 1:03
     * @param: [movementDto, imageContent]
     * @return: void
     **/
    public void publish(MovementDto movementDto, MultipartFile[] imageContent) {

        //1.从redis中获取用户id
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");

        //2.上传图片，  返回图片路径
        List<String> imagePathList = new ArrayList<>();
        try {
            for (MultipartFile file : imageContent) {
                String imagePath = ossTemplate.upload(Objects.requireNonNull(file.getOriginalFilename()), file.getInputStream());
                //返回图片路径
                imagePathList.add(imagePath);
            }

        } catch (IOException e) {
            throw new ConsumerException("图片上传失败");
        }

        //3.组装movement对象,并写入动态表
        Movement movement = new Movement();
        BeanUtils.copyProperties(movementDto, movement);
        movement.setLocationName(movementDto.getLocation());
        movement.setMedias(imagePathList);
        movement.setUserId(Convert.toLong(userId));
        movement.setCreated(Convert.toLong(new Date()));

        //3.保存动态到mongodb中  并返回动态id
        ObjectId movementId = movementServiceApi.publish(movement);

        //4.处理异步请求，写入动态时间线表，用户id，动态的id
        timeLineService.saveTimeLine(Convert.toLong(userId), movementId);

        //5.注销redis
        // redisTemplate.delete("AUTH_USER_ID");

    }

    /**
     * @description: 根据id查询个人动态
     * @author: 黄伟兴
     * @date: 2022/9/24 9:36
     * @param: [userId, page, pagesize]
     * @return: com.itheima.tanhua.vo.mongo.PageResult
     **/
    public PageResult<MovementsVo> findMovementById(Long userId, Integer page, Integer pagesize) {
        //1. 获取id查询当前登录者的动态
        if (userId == null) {
            //当前登录者的id
            userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        }

        //1.当前用户的数据  从mysql中
        UserInfo userInfo = userInfoServiceApi.findById(userId);

        //2.当前用户发布的动态数据  从mongodb中
        List<Movement> movementList = movementServiceApi.findMovementByUserId(userId, page, pagesize);

        //4.调用方法，封装数据
        PageResult<MovementsVo> pageResult = getPageResultOfVoList(page, pagesize, movementList);

        //清除redis
        // redisTemplate.delete("AUTH_USER_ID");

        return pageResult;
    }
    /**
     * @description: 根据单条动态
     * @author: 黄伟兴
     * @date: 2022/9/25 20:32
     * @param: [id]
     * @return: org.springframework.http.ResponseEntity
     **/
    public MovementsVo findRecommendById(String movementId) {

        Movement movement = movementServiceApi.findMovementByMovementId(movementId);

        if (movement != null) {
            return getPageResultOfVo(movement);
        } else {
            return null;
        }
    }


    /**
     * @description: 查询好友动态
     * @author: 黄伟兴
     * @date: 2022/9/24 11:01
     * @param: [page, pagesize]
     * @return: com.itheima.tanhua.vo.mongo.PageResult<com.itheima.tanhua.vo.mongo.MovementsVo>
     **/
    public PageResult<MovementsVo> findFriendMovements(Integer page, Integer pagesize) {
        //1.登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));

        //2.根据userid查询时间线表中对应好友的 发布动态id
        List<Object> movementIds = timeLineService.findMovementIds(userId);
        //没有发布动态，直接返回
        if (CollUtil.isEmpty(movementIds)) {
            new PageResult<MovementsVo>(page, pagesize, 0L, null);
        }

        //3.根据movementIds 查询所有的动态信息
        List<Movement> movementList = movementServiceApi.findFriendMovements(page, pagesize, movementIds);

        //5.封装信息
        PageResult<MovementsVo> pageResult = getPageResultOfVoList(page, pagesize, movementList);

        //清除redis
        //redisTemplate.delete("AUTH_USER_ID");

        return pageResult;
    }

    /**
     * @description: 查询推荐动态
     * @author: 黄伟兴
     * @date: 2022/9/25 17:54
     * @param: [page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    public PageResult<MovementsVo> recommend(Integer page, Integer pagesize) {
        //1.从redis中获取推荐数据
        //登录者的id
        Long userId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        String redisKey = Constants.MOVEMENTS_RECOMMEND + userId;
        String redisValue = redisTemplate.opsForValue().get(redisKey);

        List<Movement> movementList = new ArrayList<>();
        //2.判断推荐数据是否存在
        if (StringUtils.isEmpty(redisValue)) {
            //3.如果不存在，调用API随机构造10条动态数据
            movementList = movementServiceApi.randomMovements(pagesize);
        } else {
            //4.如果存在，处理pid数据
            String[] values = redisValue.split(",");
            //判断当前页起始条数是否小于数组总数
            if ((page - 1) * page < values.length) {
                List<Long> pids = Arrays.stream(values).skip((long) (page - 1) * pagesize).limit(pagesize).map(Long::valueOf).collect(Collectors.toList());
                //5.根据pid查询数据
                movementList = movementServiceApi.findMovementByPids(pids);
            }
        }

        //清除已经推荐过的动态
        // redisTemplate.delete(redisValue);


        //清除redis
        //  redisTemplate.delete("AUTH_USER_ID");

        //6.封装数据，返回数据
        return getPageResultOfVoList(page, pagesize, movementList);
    }



    //封装返回结果1
    private PageResult<MovementsVo> getPageResultOfVoList(Integer page, Integer pagesize, List<Movement> movementList) {
        ArrayList<MovementsVo> movementsVoList = new ArrayList<>();
        for (Movement movement : movementList) {

            //返回对象
            MovementsVo vo = new MovementsVo();
            BeanUtils.copyProperties(movement, vo, "id");
            vo.setId(movement.getId().toHexString());
            vo.setImageContent(movement.getMedias().toArray(new String[0]));
            vo.setCreateDate(RelativeDateFormat.format(new Date(movement.getCreated())));
            //TODO 临时写死，假数据
            vo.setDistance("1");

            //点赞数，评论数，喜欢数据
            vo.setCommentCount(movement.getCommentCount());
            vo.setLikeCount(movement.getLikeCount());
            vo.setLoveCount(movement.getLoveCount());

            //从redis中获取是否点赞和喜欢    1 ：点赞/喜欢
            String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");
            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
            String hashKey1 = Constants.MOVEMENT_LIKE_HASHKEY + userId;
            String hashKey2 = Constants.MOVEMENT_LOVE_HASHKEY + userId;

            Integer like = Convert.toInt(redisTemplate.opsForHash().get(key, hashKey1));
            Integer love = Convert.toInt(redisTemplate.opsForHash().get(key, hashKey2));

            //是否点赞，是否喜欢
            vo.setHasLiked((ObjectUtil.equal(like, 1) ? 1 : 0));
            vo.setHasLoved((ObjectUtil.equal(love, 1) ? 1 : 0));

            //4.查询好友的详细信息
            UserInfo userInfo = userInfoServiceApi.findById(movement.getUserId());

            //补全用户数据
            vo.setAge(userInfo.getAge());
            vo.setAvatar(userInfo.getAvatar());
            vo.setGender(userInfo.getGender());
            vo.setNickname(userInfo.getNickname());
            vo.setTags(userInfo.getTags().split(","));

            movementsVoList.add(vo);
        }

        return new PageResult<MovementsVo>(page, pagesize, 0L, movementsVoList);
    }

    //封装返回结果2
    private MovementsVo getPageResultOfVo(Movement movement) {

        //返回对象
        MovementsVo vo = new MovementsVo();
        BeanUtils.copyProperties(movement, vo, "id");
        vo.setId(movement.getId().toHexString());
        vo.setImageContent(movement.getMedias().toArray(new String[0]));
        vo.setCreateDate(RelativeDateFormat.format(new Date(movement.getCreated())));
        //TODO 临时写死，假数据
        vo.setDistance("1");

        //点赞数，评论数，喜欢数据
        vo.setCommentCount(movement.getCommentCount());
        vo.setLikeCount(movement.getLikeCount());
        vo.setLoveCount(movement.getLoveCount());

        //从redis中获取是否点赞和喜欢    1 ：点赞/喜欢
        //用户id
        String userId = redisTemplate.opsForValue().get("AUTH_USER_ID");
        String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
        String hashKey1 = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        String hashKey2 = Constants.MOVEMENT_LOVE_HASHKEY + userId;

        Integer like = Convert.toInt(redisTemplate.opsForHash().get(key, hashKey1));
        Integer love = Convert.toInt(redisTemplate.opsForHash().get(key, hashKey2));

        //是否点赞，是否喜欢
        vo.setHasLiked((ObjectUtil.equal(like, 1) ? 1 : 0));
        vo.setHasLoved((ObjectUtil.equal(love, 1) ? 1 : 0));


        //4.查询好友的详细信息
        UserInfo userInfo = userInfoServiceApi.findById(movement.getUserId());

        //补全用户数据
        vo.setAge(userInfo.getAge());
        vo.setAvatar(userInfo.getAvatar());
        vo.setGender(userInfo.getGender());
        vo.setNickname(userInfo.getNickname());
        vo.setTags(userInfo.getTags().split(","));

        return vo;
    }


    /**
     * @description: 查询首页访客列表
     * @author: 黄伟兴
     * @date: 2022/10/5 19:05
     * @param: []
     * @return: java.util.List<com.itheima.tanhua.vo.mongo.VisitorsVo>
     **/
    public List<VisitorsVo> queryVisitorsList() {
      String  currentUserId = redisTemplate.opsForValue().get("AUTH_USER_ID");

        //1.查询访问时间
        String key = Constants.VISITORS_USER;
        String hashKey = currentUserId;
        String value = (String) redisTemplate.opsForHash().get(key,hashKey);
        Long date = StringUtils.isEmpty(value)?null:Long.valueOf(value);

        //2.调用API查询数据列表 List<Visitors>
        List<Visitors> list = visitorsServiceApi.queryMyVisitors(date,Convert.toLong(currentUserId));
        if(CollUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        //3.提取用户id
        List<Long> userIds = CollUtil.getFieldValues(list, "visitorUserId", Long.class);

        //4.查看用户详情
        Map<Long, UserInfo> map = userInfoServiceApi.findByIds(userIds, null);

        //5.构造返回
        ArrayList<VisitorsVo> vos = new ArrayList<>();
        for (Visitors visitors : list) {
            UserInfo userInfo = map.get(visitors.getVisitorUserId());
            if(userInfo!=null){
                VisitorsVo vo = VisitorsVo.init(userInfo, visitors);
                vos.add(vo);
            }
        }
        return vos;
    }
}
