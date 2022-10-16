package com.itheima.tanhua.web;


import com.itheima.tanhua.dto.db.AnswerDto;
import com.itheima.tanhua.pojo.db.SoulQuestionLevel;
import com.itheima.tanhua.service.TestSoulService;
import com.itheima.tanhua.vo.mongo.ReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 测试灵魂
 */
@RestController
@RequestMapping("/testSoul")
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;

    /**
     * 获取测试问卷
     */
    @GetMapping
    public ResponseEntity<List<SoulQuestionLevel>> getTestSoul(){
        List<SoulQuestionLevel> list = testSoulService.get();
        return ResponseEntity.ok(list);
    }


    /**
     * 提交问卷, 计算成绩, 生成报告
     */
    @PostMapping
    public ResponseEntity<String> submit(@RequestBody AnswerDto answers){
        String reportId = testSoulService.submit(answers.getAnswers());
        return ResponseEntity.ok(reportId);
    }


    /**
     * 获取报告
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<ReportVo> getReport(@PathVariable("id")String id){
        ReportVo reportVo = testSoulService.getReport(id);
        return ResponseEntity.ok(reportVo);
    }
}
