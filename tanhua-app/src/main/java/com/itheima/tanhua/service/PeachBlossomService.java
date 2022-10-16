package com.itheima.tanhua.service;


import cn.hutool.core.convert.Convert;
import com.itheima.autoconfig.template.OssTemplate;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.PeachBlossomApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.PeachBlossom;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.mongo.PeachBlossomVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.itheima.tanhua.utils.Constants.PEACH_BOLSSOM_REMAIN;


@Service
public class PeachBlossomService {

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private PeachBlossomApi peachBlossomApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void send(MultipartFile soundFile) {
        try{
            // 将音频文件上传到oss上, 获得url
            String url = ossTemplate.upload(soundFile.getOriginalFilename(), soundFile.getInputStream());
            // 创建实体类对象, 封装数据
            PeachBlossom peachBlossom = new PeachBlossom();
            peachBlossom.setUserId(Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID)));
            peachBlossom.setUrl(url);
            // 远程调用保存数据
            peachBlossomApi.save(peachBlossom);
        }catch (Exception e){
            e.printStackTrace();
            throw new ConsumerException("发送语音失败");
        }
    }

    public PeachBlossomVo recive() {
        // 获得今天的格式化字符串(yyyy-MM-dd)
        String today = formatter.format(LocalDate.now());
        // 拼接redisKey(PEACH_BOLSSOM_REMAIN_2022-10-11_1)
        String redisKey = PEACH_BOLSSOM_REMAIN + today + "_" + redisTemplate.opsForValue().get(Constants.USER_ID);
        // 默认剩余接收次数为9
        Integer num = 9;
        if(redisTemplate.hasKey(redisKey)) {
            // 若redis中存在对应的key, 则表示当前用户已经接收过传音
            if(Integer.parseInt(redisTemplate.opsForValue().get(redisKey)) > 0) {
                // 若剩余次数大于0, 获取自减后数值
                num = redisTemplate.opsForValue().decrement(redisKey).intValue();
            }else{
                // 剩余次数小于0, 无法接收传音, 直接抛出异常
                throw new ConsumerException("今日接收次数已达上限");
            }
        }else{
            // redis中没有key, 则当前用户没有接收过传音, 设置剩余次数为9
            redisTemplate.opsForValue().set(redisKey, String.valueOf(9), 1, TimeUnit.DAYS);
        }
        // 远程调用随机获取一条录音
        PeachBlossom peachBlossom = peachBlossomApi.getOne(Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID)));
        // 若不存在录音, 抛出异常
        if(peachBlossom == null){
            throw new ConsumerException("暂无传音");
        }

        // 根据录音发送者id获取用户信息
        UserInfo userInfo = userInfoServiceApi.findById(peachBlossom.getUserId());
        // 封装并返回
        return PeachBlossomVo.init(userInfo, peachBlossom, num);
    }
}
