package com.itheima.tanhua.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
测灵魂-结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionsResult {

    private String conclusion;   //枚举(4)鉴定结果
    private String cover;          //枚举(4)    鉴定图片
    private Dimensions[] dimensions;    //{2}    长度: 4     维度
    private SimilarYou[] similarYou;        //{2}      长度: 10   元素唯一    与你相似

}
