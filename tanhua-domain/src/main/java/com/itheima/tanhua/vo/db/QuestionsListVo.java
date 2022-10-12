package com.itheima.tanhua.vo.db;


import com.itheima.tanhua.pojo.db.Questions;
import com.itheima.tanhua.pojo.db.QuestionsList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/*
测灵魂--试卷列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionsListVo implements Serializable {


    private String id;             //问卷编号
    private String name;           //枚举(3)    问卷名称
    private String cover;          //枚举(3)    封面
    private String level;            //枚举(3)    级别
    private Integer star;           //2 <= 值 <= 5    星别（例如：2颗星，3颗星，5颗星）
    private Questions[] questions;     //array[object]{3}       长度: 10    元素唯一    试题
    private Integer isLock;           //0 <= 值 <= 1        是否锁住（0解锁，1锁住）
    private String reportId;        //最新报告id

    public static QuestionsListVo init(QuestionsList questionsList, Questions[] questions) {
        QuestionsListVo vo = new QuestionsListVo();
        BeanUtils.copyProperties(questionsList, vo);
        vo.setQuestions(questions);
        return vo;
    }

}
