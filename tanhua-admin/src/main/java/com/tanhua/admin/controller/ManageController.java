package com.tanhua.admin.controller;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.dto.db.FreezeDto;
import com.itheima.tanhua.vo.db.UsersInfoVo;
import com.itheima.tanhua.vo.mongo.MovementsVoNew;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.tanhua.admin.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/manage")
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
    @GetMapping("/users")
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


    /**
     * @description: 根据用户id和动态状态分页查询动态
     * @author: 黄伟兴
     * @date: 2022/10/11 16:40
     * @param: [page, pagesize, uid, state]
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.mongo.PageResult < com.itheima.tanhua.vo.mongo.MovementsVo>>
     **/
    @GetMapping("/messages")
    public ResponseEntity<PageResult<MovementsVoNew>> messages(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                               @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize,
                                                               Map<String, String> param) {

        Long uid = Convert.toLong(param.get("uid"));
        Integer state = Convert.toInt(param.get("state"));


        PageResult<MovementsVoNew> result = manageService.findMovementByIdAndState(page, pagesize, uid, state);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 动态详情
     * @author: 黄伟兴
     * @date: 2022/10/11 17:18
     * @param: [movementId]
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.mongo.MovementsVo>
     **/
    @GetMapping("/messages/{id}")
    public ResponseEntity<MovementsVoNew> findMovementById(@PathVariable("id") String movementId) {
        MovementsVoNew result = manageService.findMovementById1(movementId);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:22
     * @param: [movementId]
     * @return: org.springframework.http.ResponseEntity<java.lang.String>
     **/
    @GetMapping("/messages/{id}/top")
    public ResponseEntity<String> setMovementTop(@PathVariable("id") String movementId) {
        String result = manageService.setMovementTop(movementId);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 取消动态置顶
     * @author: 黄伟兴
     * @date: 2022/10/11 17:22
     * @param: [movementId]
     * @return: org.springframework.http.ResponseEntity<java.lang.String>
     **/
    @GetMapping("/messages/{id}/untop")
    public ResponseEntity<String> divestMovementTop(@PathVariable("id") String movementId) {
        String result = manageService.divestMovementTop(movementId);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 动态通过
     * @author: 黄伟兴
     * @date: 2022/10/11 17:34
     * @param: [movementId]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/messages/pass")
    public ResponseEntity<String> approveMovement(@RequestBody String[] movementIds) {
        String result = manageService.approveMovement(movementIds);
        return ResponseEntity.ok(result);
    }


    /**
     * @description: 动态拒绝
     * @author: 黄伟兴
     * @date: 2022/10/11 17:57
     * @param: [movementIds]
     * @return: org.springframework.http.ResponseEntity<java.lang.String>
     **/
    @GetMapping("/messages/reject")
    public ResponseEntity<String> rejectMovement(@RequestBody String[] movementIds) {
        String result = manageService.rejectMovement(movementIds);
        return ResponseEntity.ok(result);
    }


}
