package com.itheima.tanhua.api.db;

import com.itheima.tanhua.pojo.db.Question;

public interface QuestionServiceApi {


    /**
     * @description: 查看陌生人问题
     * @author: 黄伟兴
     * @date: 2022/9/29 1:59
     * @param: [userId]
     * @return: com.itheima.tanhua.pojo.db.Question
     **/
    Question findByUserId(Long userId);

}
