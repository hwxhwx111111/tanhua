package com.itheima.tanhua.service;

import com.itheima.tanhua.api.db.QuestionsServiceApi;
import com.itheima.tanhua.pojo.db.Questions;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class QuestionsService {

    @DubboReference
    private QuestionsServiceApi questionsServiceApi;

    public ArrayList<Questions> list() {
        ArrayList<Questions> list = questionsServiceApi.list();
        return list;
    }
}
