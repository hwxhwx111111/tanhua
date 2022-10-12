package com.itheima.tanhua.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.tanhua.pojo.db.Answers;
import com.itheima.tanhua.pojo.db.ReportVo;
import com.itheima.tanhua.service.TestSoulService;
import com.itheima.tanhua.vo.db.QuestionsListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/*
测灵魂
 */
@RestController
@RequestMapping("/testSoul")
public class TestSoul {


    @Autowired
    private TestSoulService testSoulService;


    /**
     * @description: 测灵魂-提交问卷
     * @author: 黄伟兴
     * @date: 2022/10/11 20:20
     * @param: [answers]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping
    public ResponseEntity postQuestionnaire(@RequestBody Map param) {
       ArrayList result =(ArrayList)  param.get("answers");

        ArrayList<Answers> list = new ArrayList<>();
        for (Object o : result) {
            String s = JSON.toJSONString(o);
            Answers t = JSONObject.parseObject(s, Answers.class);
            list.add(t);
        }


        for (Answers answers : list) {
            System.out.println(answers);
        }

        return ResponseEntity.ok("提交成功!");
    }

    /**
     * @description: 测灵魂-查看结果
     * @author: 黄伟兴
     * @date: 2022/10/11 20:21
     * @param: [id]  报告id
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/report/{id}")
    public ResponseEntity report(@PathVariable String id) {
        ReportVo vo =  testSoulService.report1(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * @description: 测灵魂-问卷列表
     * @author: 黄伟兴
     * @date: 2022/10/12 18:56
     * @param: []
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping
    public ResponseEntity questionList(){
//        ArrayList<QuestionsListVo> list  = testSoulService.questionList1();
//        return ResponseEntity.ok(list);

        ArrayList<QuestionsListVo> list  = testSoulService.questionList();
        return ResponseEntity.ok(list);
    }

}
