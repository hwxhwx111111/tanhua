package com.itheima.tanhua.web;

import com.itheima.tanhua.service.HuanxinService;
import com.itheima.tanhua.vo.db.HuanxinVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huanxin")
public class HuanxinController {

    @Autowired
    private HuanxinService huanxinService;


    /**
     * @description: 查询环信用户信息
     * @author: 黄伟兴
     * @date: 2022/9/28 22:08
     * @param: []
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.db.HuanxinVo>
     **/
    @GetMapping("/user")
    public ResponseEntity<HuanxinVo> getUser() {
        HuanxinVo vo = huanxinService.findHuanxinUser();
        return ResponseEntity.ok(vo);
    }


}
