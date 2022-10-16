package com.itheima.tanhua.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.api.db.TestSoulServiceApi;
import com.itheima.tanhua.api.db.UserInfoServiceApi;
import com.itheima.tanhua.api.mongo.RecommendUserApi;
import com.itheima.tanhua.dto.db.AnswerDto;
import com.itheima.tanhua.enums.ConclusionEnum;
import com.itheima.tanhua.pojo.db.SoulQuestionLevel;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import com.itheima.tanhua.utils.Constants;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.itheima.tanhua.vo.mongo.ReportVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.itheima.tanhua.utils.Constants.REPORT;


@Service
public class TestSoulService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private TestSoulServiceApi testSoulServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public List<SoulQuestionLevel> get() {
        // 远程调用获取所有问卷
        List<SoulQuestionLevel> soulQuestionLevels = testSoulServiceApi.get();
        String uid = redisTemplate.opsForValue().get(Constants.USER_ID);
        // 循环问卷列表, 判断当前用户是否已经做过对应问卷
        for (SoulQuestionLevel soulQuestionLevel : soulQuestionLevels) {
            if(redisTemplate.hasKey(REPORT + soulQuestionLevel.getId() + "_" +uid)){
                soulQuestionLevel.setReportId(soulQuestionLevel.getId());
            }
        }
        //返回
        return soulQuestionLevels;
    }


    public String submit(List<AnswerDto.Answer> answers) {
        // 获取所有选项id
        List<String> optionIds = CollUtil.getFieldValues(answers, "optionId", String.class);
        // 根据试题id获取当前的问卷id(每次提交为同一问卷的试题, 所以通过一个选项就可以获得所属问卷)
        String id = testSoulServiceApi.getLevelId(answers.get(0).getQuestionId());
        // 远程调用计算得分并返回结论枚举对象
        ConclusionEnum conclusionEnum = testSoulServiceApi.getReport(optionIds);
        // 将结论枚举名存入redis中
        redisTemplate.opsForValue().set(REPORT + id + "_" + redisTemplate.opsForValue().get(Constants.USER_ID), conclusionEnum.name());
        // 返回问卷id
        return id;
    }

    public ReportVo getReport(String levelId) {
        // 从redis中获得结论枚举对象名
        String name = redisTemplate.opsForValue().get(REPORT + levelId + "_" + redisTemplate.opsForValue().get(Constants.USER_ID));
        if(StrUtil.isNotBlank(name)) {
            // 对象名非空, 根据对象名获取枚举对象
            ConclusionEnum conclusionEnum = ConclusionEnum.valueOf(name);
            // 创建报告视图对象
            ReportVo reportVo = new ReportVo();
            // 封装结论文字和图片链接
            reportVo.setConclusion(conclusionEnum.getConclusion());
            reportVo.setCover(conclusionEnum.getCover());
            // 随机生成四维数据并封装进报告视图
            Random random = new Random();
            List<ReportVo.Dimension> dimensionList = new ArrayList<>();
            dimensionList.add(new ReportVo.Dimension("外向", (random.nextInt(40) + 60) + "%"));
            dimensionList.add(new ReportVo.Dimension("判断", (random.nextInt(40) + 60) + "%"));
            dimensionList.add(new ReportVo.Dimension("抽象", (random.nextInt(40) + 60) + "%"));
            dimensionList.add(new ReportVo.Dimension("理性", (random.nextInt(40) + 60) + "%"));
            reportVo.setDimensions(dimensionList);
            // 获取当前用户的10个推荐用户的用户信息, 封装进报告视图
            PageResult<RecommendUser> pageInfo = recommendUserApi.getRecommendUserPage(Convert.toLong(redisTemplate.opsForValue().get(Constants.USER_ID)), 1, 10);
            List<Long> userIdList = CollUtil.getFieldValues(pageInfo.getItems(), "userId", Long.class);
            if(CollUtil.isNotEmpty(userIdList)) {
                reportVo.setSimilarYou(userInfoServiceApi.getUserInfo(userIdList, null));
            }else{
                reportVo.setSimilarYou(Collections.emptyList());
            }
            // 返回报告视图对象
            return reportVo;
        }
        // 若对象名为空, 则不存在对应报告, 返回null
        return null;
    }
}
