package com.itheima.tanhua.api;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.tanhua.api.db.QuestionServiceApi;
import com.itheima.tanhua.mapper.QuestionMapper;
import com.itheima.tanhua.pojo.db.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionServiceImpl implements QuestionServiceApi {

    @Autowired
    private QuestionMapper questionsMapper;

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
    @Override
    public String findStrangerQuestions(Long userId) {

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getUserId, userId);
        Question question = questionsMapper.selectOne(wrapper);
        String txt = question.getTxt();
        return txt;
    }
    @Override
    public void saveOrUpdate(Long uid, String txt) {
        //查询当前登录用户的问题是否为空
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getUserId, uid);
        Question question= questionsMapper.selectOne(wrapper);

        //为空,新增
        if (ObjectUtil.isNull(question)){
            question = new Question();
            question.setTxt(txt);
            question.setUserId(uid);
            questionsMapper.insert(question);
        }else {
            //不为空,更新
            question.setTxt(txt);
            questionsMapper.updateById(question);
        }
    }
}
