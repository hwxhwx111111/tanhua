package com.itheima.tanhua.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 添加好友时提的问题
 * @author: 黄伟兴
 * @date: 2022/9/27 17:15
 * @param:
 * @return:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BasePojo{
    private Long userId;
    private String txt;
}
