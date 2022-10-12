package com.itheima.tanhua.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.tanhua.api.db.QuestionsServiceApi;
import com.itheima.tanhua.mapper.QuestionsMapper;
import com.itheima.tanhua.pojo.db.Questions;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@DubboService
public class QuestionsServiceImpl implements QuestionsServiceApi {

    @Autowired
    private QuestionsMapper mapper;

    @Override
    public ArrayList<Questions> list() {

        List<Questions> list = mapper.selectList(new LambdaQueryWrapper<>());

        return (ArrayList<Questions>) list;
    }
}
