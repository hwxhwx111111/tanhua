package com.itheima.tanhua.service;

import com.itheima.tanhua.api.db.QuestionsListServiceApi;
import com.itheima.tanhua.pojo.db.*;
import com.itheima.tanhua.utils.Conclusion;
import com.itheima.tanhua.utils.Cover;
import com.itheima.tanhua.utils.DimensionsConstant;
import com.itheima.tanhua.utils.OptionConstant;
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
    public ArrayList<QuestionsListVo> questionList() {

        ArrayList<QuestionsList> list = questionsListServiceApi.questionList();

        ArrayList<QuestionsListVo> voList = new ArrayList<>();
        for (QuestionsList questionsList1 : list) {
            ArrayList<Questions> questionsList = questionsService.list();

            Questions[] listt = new Questions[10];
            for (int i = 0; i < questionsList.size(); i++) {
                listt[i] = questionsList.get(i);
            }

            QuestionsListVo vo = QuestionsListVo.init(questionsList1, listt);
            voList.add(vo);
        }

        return voList;
    }


    public ArrayList<QuestionsListVo> questionList1() {

        QuestionsListVo q1 = new QuestionsListVo();
        QuestionsListVo q2 = new QuestionsListVo();
        QuestionsListVo q3 = new QuestionsListVo();
        QuestionsListVo q4 = new QuestionsListVo();

        q1.setCover(Cover.QN_COVER_01);
        q2.setCover(Cover.QN_COVER_02);
        q3.setCover(Cover.QN_COVER_03);
        q4.setCover(Cover.QN_COVER_03);

        q1.setLevel(Cover.LEVEL1);
        q2.setLevel(Cover.LEVEL2);
        q3.setLevel(Cover.LEVEL3);
        q4.setLevel(Cover.LEVEL3);

        q1.setName(Cover.NAME1);
        q2.setName(Cover.NAME2);
        q3.setName(Cover.NAME3);
        q4.setName(Cover.NAME3);

        q1.setStar(2);
        q2.setStar(3);
        q3.setStar(4);
        q4.setStar(4);

        q1.setIsLock(0);
        q2.setIsLock(0);
        q3.setIsLock(1);
        q4.setIsLock(1);

        q1.setReportId(String.valueOf(1));
        q2.setReportId(String.valueOf(2));
        q3.setReportId(String.valueOf(3));
        q4.setReportId(String.valueOf(4));

        q1.setId(String.valueOf(1));
        q2.setId(String.valueOf(2));
        q3.setId(String.valueOf(3));
        q4.setId(String.valueOf(4));

        //第一题选项
        Options[] options1 = new Options[7];
        options1[0] = new Options(String.valueOf(1), OptionConstant.OPTION_1_A);
        options1[1] = new Options(String.valueOf(2), OptionConstant.OPTION_1_B);
        options1[2] = new Options(String.valueOf(3), OptionConstant.OPTION_1_C);

        //第一题问题
        Questions questions1 = new Questions(String.valueOf(1), OptionConstant.QUESTION_1, options1);


        //第2题选项
        Options[] options2 = new Options[7];
        options2[0] = new Options(String.valueOf(1), OptionConstant.OPTION_2_A);
        options2[1] = new Options(String.valueOf(2), OptionConstant.OPTION_2_B);
        options2[2] = new Options(String.valueOf(3), OptionConstant.OPTION_2_C);
        options2[3] = new Options(String.valueOf(4), OptionConstant.OPTION_2_D);
        options2[4] = new Options(String.valueOf(5), OptionConstant.OPTION_2_E);
        //第2题问题
        Questions questions2 = new Questions(String.valueOf(2), OptionConstant.QUESTION_2, options2);


        //第3题选项
        Options[] options3 = new Options[7];

        options3[0] = new Options(String.valueOf(1), OptionConstant.OPTION_3_A);
        options3[1] = new Options(String.valueOf(2), OptionConstant.OPTION_3_B);
        options3[2] = new Options(String.valueOf(3), OptionConstant.OPTION_3_C);
        options3[3] = new Options(String.valueOf(4), OptionConstant.OPTION_3_D);
        options3[4] = new Options(String.valueOf(5), OptionConstant.OPTION_3_E);
        //第3题问题
        Questions questions3 = new Questions(String.valueOf(3), OptionConstant.QUESTION_3, options3);


        //第4题选项
        Options[] options4 = new Options[7];
        options4[0] = new Options(String.valueOf(1), OptionConstant.OPTION_4_A);
        options4[1] = new Options(String.valueOf(2), OptionConstant.OPTION_4_B);
        options4[2] = new Options(String.valueOf(3), OptionConstant.OPTION_4_C);
        options4[3] = new Options(String.valueOf(4), OptionConstant.OPTION_4_D);
        //第4题问题
        Questions questions4 = new Questions(String.valueOf(4), OptionConstant.QUESTION_4, options4);


        //第5题选项
        Options[] options5 = new Options[7];
        options5[0] = new Options(String.valueOf(1), OptionConstant.OPTION_5_A);
        options5[1] = new Options(String.valueOf(2), OptionConstant.OPTION_5_B);
        options5[2] = new Options(String.valueOf(3), OptionConstant.OPTION_5_C);
        options5[3] = new Options(String.valueOf(4), OptionConstant.OPTION_5_D);
        //第5题问题
        Questions questions5 = new Questions(String.valueOf(5), OptionConstant.QUESTION_5, options5);


        //第6题选项
        Options[] options6 = new Options[7];
        options6[0] = new Options(String.valueOf(1), OptionConstant.OPTION_6_A);
        options6[1] = new Options(String.valueOf(2), OptionConstant.OPTION_6_B);
        options6[2] = new Options(String.valueOf(3), OptionConstant.OPTION_6_C);
        //第6题问题
        Questions questions6 = new Questions(String.valueOf(6), OptionConstant.QUESTION_6, options6);


        //第7题选项
        Options[] options7 = new Options[7];
        options7[0] = new Options(String.valueOf(1), OptionConstant.OPTION_7_A);
        options7[1] = new Options(String.valueOf(2), OptionConstant.OPTION_7_B);
        options7[2] = new Options(String.valueOf(3), OptionConstant.OPTION_7_C);
        //第7题问题
        Questions questions7 = new Questions(String.valueOf(7), OptionConstant.QUESTION_7, options7);

        //第8题选项
        Options[] options8 = new Options[8];
        options8[0] = new Options(String.valueOf(1), OptionConstant.OPTION_8_A);
        options8[1] = new Options(String.valueOf(2), OptionConstant.OPTION_8_B);
        options8[2] = new Options(String.valueOf(3), OptionConstant.OPTION_8_C);
        options8[3] = new Options(String.valueOf(4), OptionConstant.OPTION_8_D);
        options8[4] = new Options(String.valueOf(5), OptionConstant.OPTION_8_E);
        options8[5] = new Options(String.valueOf(6), OptionConstant.OPTION_8_F);
        //第8题问题
        Questions questions8 = new Questions(String.valueOf(8), OptionConstant.QUESTION_8, options8);

        //第9题选项
        Options[] options9 = new Options[7];
        options9[0] = new Options(String.valueOf(1), OptionConstant.OPTION_9_A);
        options9[1] = new Options(String.valueOf(2), OptionConstant.OPTION_9_B);
        options9[2] = new Options(String.valueOf(3), OptionConstant.OPTION_9_C);
        options9[3] = new Options(String.valueOf(4), OptionConstant.OPTION_9_D);
        options9[4] = new Options(String.valueOf(5), OptionConstant.OPTION_9_E);

        //第9题问题
        Questions questions9 = new Questions(String.valueOf(9), OptionConstant.QUESTION_9, options9);

        //第10选项
        Options[] options10 = new Options[7];
        options10[0] = new Options(String.valueOf(1), OptionConstant.OPTION_10_A);
        options10[1] = new Options(String.valueOf(2), OptionConstant.OPTION_10_B);
        options10[2] = new Options(String.valueOf(3), OptionConstant.OPTION_10_C);
        options10[3] = new Options(String.valueOf(4), OptionConstant.OPTION_10_D);
        options10[4] = new Options(String.valueOf(5), OptionConstant.OPTION_10_E);
        options10[5] = new Options(String.valueOf(6), OptionConstant.OPTION_10_F);
        //第10题问题
        Questions questions10 = new Questions(String.valueOf(10), OptionConstant.QUESTION_10, options10);

        //10个问题和答案
        Questions[] questionsArr = new Questions[]{questions1, questions2, questions3,
                questions4, questions5, questions6,
                questions7, questions8, questions9, questions10};


        q1.setQuestions(questionsArr);
        q2.setQuestions(questionsArr);
        q3.setQuestions(questionsArr);
        q4.setQuestions(questionsArr);


        ArrayList<QuestionsListVo> list = new ArrayList<>();
        list.add(q1);
        list.add(q2);
        list.add(q3);

        return list;
    }


    /**
     * @description: 测灵魂-查看结果
     * @author: 黄伟兴
     * @date: 2022/10/13 10:03
     * @param: [id]
     * @return: java.util.ArrayList<com.itheima.tanhua.pojo.db.QuestionsList>
     **/
    public ArrayList<ReportVo> report(String id) {

        ReportVo vo1 = new ReportVo();
        ReportVo vo2 = new ReportVo();
        ReportVo vo3 = new ReportVo();
        ReportVo vo4 = new ReportVo();

        vo1.setConclusion(Conclusion.FOX);
        vo2.setConclusion(Conclusion.LION);
        vo3.setConclusion(Conclusion.OWL);
        vo4.setConclusion(Conclusion.RABBIT);

        vo1.setCover(Cover.FOX);
        vo2.setCover(Cover.LION);
        vo3.setCover(Cover.OWL);
        vo4.setCover(Cover.RABBIT);

        vo1.setDimensions(new Dimensions[4]);
        vo2.setDimensions(new Dimensions[4]);
        vo3.setDimensions(new Dimensions[4]);
        vo4.setDimensions(new Dimensions[4]);

        vo1.setSimilarYou(new SimilarYou[10]);
        vo2.setSimilarYou(new SimilarYou[10]);
        vo3.setSimilarYou(new SimilarYou[10]);
        vo4.setSimilarYou(new SimilarYou[10]);

        ArrayList<ReportVo> list = new ArrayList<>();
        list.add(vo1);
        list.add(vo2);
        list.add(vo3);
        list.add(vo4);

        return list;

    }

    public ReportVo report1(String id) {

        ReportVo vo1 = new ReportVo();
        vo1.setConclusion(Conclusion.FOX);
        vo1.setCover(Cover.FOX);

        Dimensions d1 = new Dimensions(DimensionsConstant.KEY1, DimensionsConstant.VALUE1);
        Dimensions d2 = new Dimensions(DimensionsConstant.KEY2, DimensionsConstant.VALUE2);
        Dimensions d3 = new Dimensions(DimensionsConstant.KEY3, DimensionsConstant.VALUE3);
        Dimensions d4 = new Dimensions(DimensionsConstant.KEY4, DimensionsConstant.VALUE4);
        Dimensions[] ds = {d1, d2, d3, d4};
        vo1.setDimensions(ds);


        SimilarYou s1 = new SimilarYou(1, Cover.AVATAR1);
        SimilarYou s2 = new SimilarYou(2, Cover.AVATAR2);
        SimilarYou s3 = new SimilarYou(3, Cover.AVATAR3);
        SimilarYou s4 = new SimilarYou(4, Cover.AVATAR4);

        SimilarYou s5 = new SimilarYou(1, Cover.AVATAR2);
        SimilarYou s6 = new SimilarYou(2, Cover.AVATAR3);
        SimilarYou s7 = new SimilarYou(3, Cover.AVATAR4);
        SimilarYou s8 = new SimilarYou(4, Cover.AVATAR1);

        SimilarYou s9 = new SimilarYou(1, Cover.AVATAR3);
        SimilarYou s10 = new SimilarYou(2, Cover.AVATAR4);

        SimilarYou[] ss = new SimilarYou[]{s1, s2, s3, s4, s5, s6, s7, s8, s9, s10};
        vo1.setSimilarYou(ss);

        return vo1;
    }
}
