package com.itheima.tanhua.pojo.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarYou {

    private Integer id;    //1 <= 值 <= 10000   用户编号
    private String avatar;   //枚举(4)  头像

}
