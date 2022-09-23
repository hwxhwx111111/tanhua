package com.itheima.tanhua.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.tanhua.api.db.QuestionServiceApi;
import com.itheima.tanhua.mapper.QuestionsMapper;
import com.itheima.tanhua.pojo.db.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionsServiceImpl implements QuestionServiceApi {

    @Autowired
    private QuestionsMapper questionsMapper;

    /**
     * @description: 查看陌生人问题
     * @author: 黄伟兴
     * @date: 2022/9/29 0:42
     * @param: []
     * @return: com.itheima.tanhua.pojo.db.Question
     **/
    @Override
    public Question findByUserId(Long userId) {

        LambdaQueryWrapper<Question> query=new LambdaQueryWrapper<>();
        query.eq(Question::getUserId,userId);
        return questionsMapper.selectOne(query);
    }

}
