package com.tanhua.admin.controller;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.dto.db.FreezeDto;
import com.itheima.tanhua.vo.db.UsersInfoVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.tanhua.admin.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("manage")
public class ManageController {
    @Autowired
    private ManageService manageService;


    /**
     * 用户数据翻页
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = manageService.findPageUsers(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 用户基本资料
     *
     * @param userID
     * @return
     */
    @GetMapping("/users/{userID}")
    public ResponseEntity findByUserId(@PathVariable String userID) {
        UsersInfoVo usersInfoVo = manageService.findByUserId(userID);
        return ResponseEntity.ok(usersInfoVo);
    }

    /**
     * 冻结用户
     *
     * @param freezeDto
     * @return
     */
    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody FreezeDto freezeDto) {
        manageService.freeze(freezeDto);
        return ResponseEntity.ok("冻结成功");
    }

    /**
     * 冻结用户
     *
     * @param param : userId-用户id reasonsForThawing-解冻原因
     * @return
     */
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody Map param) {
        Integer userId = Convert.toInt(param.get("userId"));
        String frozenRemarks = Convert.toStr(param.get("frozenRemarks"));
        manageService.unfreeze(userId, frozenRemarks);
        return ResponseEntity.ok("解冻成功");
    }
}
