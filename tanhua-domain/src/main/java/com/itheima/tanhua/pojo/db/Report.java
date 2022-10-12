package com.itheima.tanhua.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/*
测灵魂-结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report implements Serializable {

    private String id;
    private String conclusion;   //枚举(4)鉴定结果
    private String cover;          //枚举(4)    鉴定图片

}
