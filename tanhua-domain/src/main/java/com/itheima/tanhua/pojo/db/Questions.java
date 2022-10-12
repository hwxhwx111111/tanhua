package com.itheima.tanhua.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
试题
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Questions implements Serializable {
    private String id;              //       试题编号
    private String   question  ;   //题目          第一题
    private Options[]  options;       //元素唯一      选项

}
