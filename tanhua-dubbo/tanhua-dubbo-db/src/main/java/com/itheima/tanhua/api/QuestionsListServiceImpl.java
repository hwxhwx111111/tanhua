package com.itheima.tanhua.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.tanhua.api.db.QuestionsListServiceApi;
import com.itheima.tanhua.mapper.QuestionsListMapper;
import com.itheima.tanhua.pojo.db.QuestionsList;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@DubboService
public class QuestionsListServiceImpl implements QuestionsListServiceApi {

    @Autowired
    private QuestionsListMapper mapper;


    @Override
    public ArrayList<QuestionsList> questionList() {
        ArrayList<QuestionsList> list = (ArrayList<QuestionsList>) mapper.selectList(new LambdaQueryWrapper<>());
        return list;
    }
}
