package com.tanhua.admin.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.pojo.Analysis;
import com.tanhua.admin.mapper.AnalysisMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AnalysisService {
    @Autowired
    private AnalysisMapper analysisMapper;


    /**
     * 查询活跃用户的数量
     */
    public Long findActiveUserCount(DateTime dateTime, Integer num) {
        return getUserCount(dateTime, num, "num_active");
    }


    /**
     * 查询注册用户的数量
     */
    public Long findRegisterUserCount(DateTime dateTime, int num) {
        return getUserCount(dateTime, num, "num_registered");

    }

    /**
     * 查询登录用户的数量
     */

    public Long queryLoginUserCount(DateTime dateTime, int num) {
        return getUserCount(dateTime, num, "num_login");

    }

    private Long getUserCount(DateTime dateTime, int num, String type) {
        Integer counts = 0;
        DateTime start = DateUtil.offsetDay(dateTime, num);
        List<Integer> list = analysisMapper.findUserCount(start, dateTime, type);
        for (Integer integer : list) {
            counts = integer + counts;

        }
        return Convert.toLong(counts);
    }

    public List<Analysis> findUsers(DateTime startDate, DateTime endDate, String type) {
        List<Analysis> list = new ArrayList<>();
        if (StrUtil.equals(type, "101")) {
            list = analysisMapper.findUsers(startDate, endDate, "num_registered");
        } else if (StrUtil.equals(type, "102")) {
            list = analysisMapper.findUsers(startDate, endDate, "num_active");
        } else if (StrUtil.equals(type, "103")) {
            list = analysisMapper.findUsers(startDate, endDate, "num_retention1d");
        }
        return list;
    }
}
