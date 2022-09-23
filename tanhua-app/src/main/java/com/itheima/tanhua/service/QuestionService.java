package com.itheima.tanhua.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.itheima.autoconfig.template.HuanXinTemplate;
import com.itheima.tanhua.api.db.QuestionServiceApi;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.exception.ConsumerException;
import com.itheima.tanhua.pojo.db.Question;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.utils.Constants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private QuestionServiceApi questionServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    /**
     * @description: 回复陌生人问题
     * @author: 黄伟兴
     * @date: 2022/9/29 0:59
     * @param: [userId, reply]   对方id，回复内容
     * @return: void
     **/
    public void replyQuestions(Long userId, String reply) {
        //1、构造消息数据
        Long currentUserId = Convert.toLong(redisTemplate.opsForValue().get("AUTH_USER_ID"));
        //2.根据用户id查询当前用户信息
        UserInfo userInfo = userInfoServiceApi.findById(currentUserId);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", currentUserId);
        map.put("huanXinId", Constants.HX_USER_PREFIX + currentUserId);
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", strangerQuestions(userId));//对方id  查询对方的陌生人问题
        map.put("reply", reply);
        String message = JSON.toJSONString(map);


        //2、调用template对象，发送消息
        Boolean messageOk = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId, message);//1、接受方的环信id，2、消息
        if (!messageOk) {
            throw new ConsumerException("消息发送失败");
        }
    }

    /**
     * @description: 查看陌生人问题
     * @author: 黄伟兴
     * @date: 2022/9/27 14:02
     * @param: [userId]
     * @return: org.springframework.http.ResponseEntity
     **/
    public String strangerQuestions(Long userId) {
        Question question = questionServiceApi.findByUserId(userId);
        if (ObjectUtil.isNull(question)) {
            question = new Question();
            question.setTxt("1+1=？");
        }
        return question.getTxt();
    }
}
