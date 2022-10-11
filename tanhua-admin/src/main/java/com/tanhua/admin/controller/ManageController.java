package com.tanhua.admin.controller;

import com.itheima.tanhua.vo.mongo.PageResult;
import com.tanhua.admin.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manage")
public class ManageController {
    @Autowired
    private ManageService manageService;


    @GetMapping("users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize)
    {
        PageResult result =  manageService.findPageUsers(page,pagesize);
        return ResponseEntity.ok(result);
    }
}
