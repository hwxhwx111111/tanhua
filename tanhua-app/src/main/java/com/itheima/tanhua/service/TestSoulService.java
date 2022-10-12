package com.itheima.tanhua.service;

import com.itheima.tanhua.api.db.QuestionsListServiceApi;
import com.itheima.tanhua.pojo.db.Questions;
import com.itheima.tanhua.pojo.db.QuestionsList;
import com.itheima.tanhua.vo.db.QuestionsListVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TestSoulService {

    @DubboReference
    private QuestionsListServiceApi questionsListServiceApi;

    @Autowired
    private QuestionsService questionsService;

    /**
     * @description: 测灵魂-问卷列表
     * @author: 黄伟兴
     * @date: 2022/10/12 19:00
     * @param: []
     * @return: java.util.ArrayList<com.itheima.tanhua.pojo.db.QuestionsList>
     **/
    public ArrayList<QuestionsList> questionList() {

        ArrayList<QuestionsList> list = questionsListServiceApi.questionList();

        for (QuestionsList questionsList1 : list) {
            ArrayList<Questions> questionsList = questionsService.list();

            Questions[] listt = new Questions[10];
            for (int i = 0; i < questionsList.size(); i++) {
                listt[i] = questionsList.get(i);
            }

            QuestionsListVo.init(questionsList1,listt);
        }


        return list;
    }
}
