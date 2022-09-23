package com.itheima.tanhua.web;

import com.itheima.tanhua.service.RecommendService;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.TodayBestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private RecommendService recommendService;

    /**
     * @description: 今日佳人
     * @author: 黄伟兴
     * @date: 2022/9/23 1:38
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/todayBest")
    public ResponseEntity todayBest(@RequestHeader("Authorization") String token){
        TodayBestVo todayBestVo = recommendService.todayBest(token);
        return  ResponseEntity.ok(todayBestVo);
    }

    /**
     * @description: 佳人推荐
     * @author: 黄伟兴
     * @date: 2022/9/23 1:50
     * @param: [token, page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("recommendation")
    public ResponseEntity recommendation(@RequestHeader("Authorization") String token,
                                         @RequestParam(value = "page",defaultValue = "1") Integer page,
                                         @RequestParam(value = "pagesize",defaultValue = "10") Integer pagesize){

       PageResult pageResult =  recommendService.recommendation(token,page,pagesize);
        return ResponseEntity.ok(pageResult);
    }
}
