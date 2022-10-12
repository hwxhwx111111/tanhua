package com.tanhua.admin.controller;

import com.itheima.tanhua.vo.db.AnalysisSummaryVo;
import com.tanhua.admin.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("dashboard")
public class DashboardController {
    @Autowired
    private DashBoardService dashBoardService;

    @GetMapping("/summary")
    public ResponseEntity summary() {
        AnalysisSummaryVo summary = dashBoardService.getSummary();
        return ResponseEntity.ok(summary);
    }
    @GetMapping("users")
    public ResponseEntity users(String sd, String ed, String type){

        Map map = dashBoardService.users(sd,ed,type);
        return ResponseEntity.ok(map);
    }

}
