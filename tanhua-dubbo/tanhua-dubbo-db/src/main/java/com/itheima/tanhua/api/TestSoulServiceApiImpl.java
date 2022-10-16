package com.itheima.tanhua.api;


import com.itheima.tanhua.api.db.TestSoulServiceApi;
import com.itheima.tanhua.enums.ConclusionEnum;
import com.itheima.tanhua.mapper.SoulOptionMapper;
import com.itheima.tanhua.mapper.SoulQuestionLevelMapper;
import com.itheima.tanhua.mapper.SoulQuestionMapper;
import com.itheima.tanhua.pojo.db.SoulQuestionLevel;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@DubboService
public class TestSoulServiceApiImpl implements TestSoulServiceApi {

    @Autowired
    private SoulQuestionLevelMapper soulQuestionLevelMapper;

    @Autowired
    private SoulQuestionMapper soulQuestionMapper;

    @Autowired
    private SoulOptionMapper soulOptionMapper;

    @Override
    public List<SoulQuestionLevel> get() {
        return soulQuestionLevelMapper.get();
    }

    @Override
    public ConclusionEnum getReport(List<String> optionIds) {
        Integer score = soulOptionMapper.calculateScore(optionIds);
        ConclusionEnum conclusionEnum;
        if(score < 21){
            conclusionEnum = ConclusionEnum.OWL;
        }else if(score < 40){
            conclusionEnum = ConclusionEnum.RABBIT;
        }else if(score < 55){
            conclusionEnum = ConclusionEnum.FOX;
        }else{
            conclusionEnum = ConclusionEnum.LION;
        }

        return conclusionEnum;
    }

    @Override
    public String getLevelId(String questionId) {
        return soulQuestionMapper.getLevelId(questionId);
    }
}
