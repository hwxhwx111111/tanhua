package com.tanhua.admin.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.tanhua.pojo.Analysis;
import com.itheima.tanhua.vo.db.AnalysisSummaryVo;
import com.itheima.tanhua.vo.db.AnalysisUsersVo;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class DashBoardService {
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private AnalysisMapper analysisMapper;
    @Autowired
    private LogMapper logMapper;

    public AnalysisSummaryVo getSummary() {

        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        DateTime dateTime = DateUtil.parseDate(format);

        log.info("dataTime:{}", dateTime);
        //累计用户数
        analysisSummaryVo.setCumulativeUsers(logMapper.findAllUser());

        //过去30天活跃用户
        analysisSummaryVo.setActivePassMonth(this.analysisService.findActiveUserCount(dateTime, -30));

        //过去7天活跃用户
        analysisSummaryVo.setActivePassWeek(this.analysisService.findActiveUserCount(dateTime, -7));

        //今日活跃用户
        analysisSummaryVo.setActiveUsersToday(this.analysisService.findActiveUserCount(dateTime, 0));

        //今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setActiveUsersTodayRate(computeRate(
                analysisSummaryVo.getActiveUsersToday(),
                this.analysisService.findActiveUserCount(dateTime, -1) - analysisSummaryVo.getActiveUsersToday()
        ));
        //今日新增用户
        analysisSummaryVo.setNewUsersToday(this.analysisService.findRegisterUserCount(dateTime, 0));

        //今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setNewUsersTodayRate(computeRate(
                analysisSummaryVo.getNewUsersToday(),
                this.analysisService.findRegisterUserCount(dateTime, -1) - analysisSummaryVo.getNewUsersToday()
        ));

        //今日登录次数
        analysisSummaryVo.setLoginTimesToday(this.analysisService.queryLoginUserCount(dateTime, 0));

        //今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setLoginTimesTodayRate(computeRate(
                analysisSummaryVo.getLoginTimesToday(),
                this.analysisService.queryLoginUserCount(dateTime, -1) - analysisSummaryVo.getLoginTimesToday()
        ));


        return analysisSummaryVo;


    }


    private static BigDecimal computeRate(Long current, Long last) {
        BigDecimal result;
        if (last == 0) {
            // 当上一期计数为零时，此时环比增长为倍数增长
            result = new BigDecimal((current - last) * 100);
        } else {
            result = BigDecimal.valueOf((current - last) * 100).divide(BigDecimal.valueOf(last), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        return result;


    }


    public Map users(String start, String end, String type) {
        //处理时间格式
        DateTime startDate = DateUtil.date(Convert.toLong(start));
        DateTime endDate = DateUtil.date(Convert.toLong(end));

        //查询本年的
        List<Analysis> thisList =  analysisService.findUsers(startDate,endDate,type);
        //查询去年的
        List<Analysis> lastList =  analysisService.findUsers(DateUtil.offsetMonth(startDate,12),DateUtil.offsetMonth(endDate,12),type);

        //数据处理
        List<AnalysisUsersVo> thisYear = new ArrayList<>();
        for (Analysis analysis : thisList) {
            AnalysisUsersVo vo = new AnalysisUsersVo();
            vo.setTitle(DateUtil.formatDate(analysis.getRecordDate()));
            if (StrUtil.equals(type, "101")) {
                vo.setAmount(analysis.getNumRegistered());
            } else if (StrUtil.equals(type, "102")) {
                vo.setAmount(analysis.getNumActive());
            } else if (StrUtil.equals(type, "103")) {
                vo.setAmount(analysis.getNumRetention1d());
            }
            thisYear.add(vo);
        }
        List<AnalysisUsersVo> lastYear = new ArrayList<>();
        for (Analysis analysis : lastList) {
            AnalysisUsersVo vo = new AnalysisUsersVo();
            vo.setTitle(DateUtil.formatDate(analysis.getRecordDate()));
            if (StrUtil.equals(type, "101")) {
                vo.setAmount(analysis.getNumRegistered());
            } else if (StrUtil.equals(type, "102")) {
                vo.setAmount(analysis.getNumActive());
            } else if (StrUtil.equals(type, "103")) {
                vo.setAmount(analysis.getNumRetention1d());
            }
            lastYear.add(vo);
        }
        //封装对象
        Map map = new HashMap<>();
        map.put("thisYear",thisYear);
        map.put("lastYear",lastYear);

        return map;
    }
}
