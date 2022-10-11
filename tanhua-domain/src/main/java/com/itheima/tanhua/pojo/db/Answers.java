package com.itheima.tanhua.pojo.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
测灵魂-每一题的答案
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answers {
   private String   questionId  ;   //试题编号   第一题
   private String   optionId;       //选项编号    选A

}
