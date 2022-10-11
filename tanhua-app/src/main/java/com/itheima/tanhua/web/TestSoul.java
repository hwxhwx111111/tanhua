package com.itheima.tanhua.web;

import org.mockito.Answers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
测灵魂
 */
@RestController
@RequestMapping("/testSoul")
public class TestSoul {


    /**
     * @description: 测灵魂-提交问卷
     * @author: 黄伟兴
     * @date: 2022/10/11 20:20
     * @param: [answers]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping
    public ResponseEntity postQuestionnaire(@RequestBody Answers answers){
        return null;
    }

    /**
     * @description: 测灵魂-查看结果
     * @author: 黄伟兴
     * @date: 2022/10/11 20:21
     * @param: [id]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/report/{id}")
    public ResponseEntity report(@PathVariable String id){

        return null;
    }


}
