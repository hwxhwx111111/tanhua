package com.itheima.tanhua.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dimensions {


    private String key;         //枚举(4)  维度项（外向，判断，抽象，理性）
    private String value;      //枚举(4)   维度值

}
