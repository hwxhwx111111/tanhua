package com.itheima.tanhua.web;

import com.itheima.tanhua.api.mongo.RecommendServiceApi;
import com.itheima.tanhua.service.FateValueService;
import com.itheima.tanhua.vo.mongo.ApifoxModelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class FateValueController {

    @Autowired
    private FateValueService fateValueService;

    private RecommendServiceApi recommendServiceApi;

    /**
     * 缘分值
     * @param id
     * @return
     */
    @GetMapping("/{id}/fateValue")
    public ResponseEntity FateValue(@PathVariable String id){
        ApifoxModelVo apifoxModelVo= fateValueService.FateValue(id);
        return ResponseEntity.ok(apifoxModelVo);
    }

    /**
     * 是否喜欢
     * @param uid
     * @return
     */
    @GetMapping("/{uid}/alreadyLove")
    public ResponseEntity alreadyLove(@PathVariable String uid){
        boolean flag=fateValueService.alreadyLove(uid);
        return ResponseEntity.ok(flag);
    }
}
