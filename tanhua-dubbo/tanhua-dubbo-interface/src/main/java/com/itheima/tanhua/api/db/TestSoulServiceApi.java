package com.itheima.tanhua.api.db;


import com.itheima.tanhua.enums.ConclusionEnum;
import com.itheima.tanhua.pojo.db.SoulQuestionLevel;

import java.util.List;


public interface TestSoulServiceApi {
    List<SoulQuestionLevel> get();

    ConclusionEnum getReport(List<String> optionIds);

    String getLevelId(String questionId);
}
