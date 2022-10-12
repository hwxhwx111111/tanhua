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
                                                               @RequestParam(required = false) String uid,
                                                               @RequestParam(required = false) String state,
                                                               @RequestParam(required = false) String sd,
                                                               @RequestParam(required = false) String ed,
                                                               @RequestParam String sortProp,
                                                               @RequestParam String sortOrder) {
        Long userId = Convert.toLong(uid);
        Integer state1 = Convert.toInt(state);
        //PageResult<MovementsVoNew> result = manageService.findMovementByIdAndState(page, pagesize, id1, state1);
        PageResult<MovementsVoNew> result = manageService.findMovementByIdAndState(page, pagesize, userId, state1,sortProp,sortOrder);
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
    @PostMapping("/messages/{id}/top")
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
    @PostMapping("/messages/{id}/untop")
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
    @PostMapping("/messages/pass")
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
    @PostMapping("/messages/reject")
    public ResponseEntity<String> rejectMovement(@RequestBody String[] movementIds) {
        String result = manageService.rejectMovement(movementIds);
        return ResponseEntity.ok(result);
    }

    /**
     * 评论列表
     * @param page
     * @param pagesize
     * @param messageID
     * @return
     */
    @GetMapping({"messages/comments"})
    public ResponseEntity comments(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize, String messageID) {
        PageResult result = manageService.comments(page, pagesize, messageID);
        return ResponseEntity.ok(result);
    }

    /**
     * 视频记录
     * @param page
     * @param pagesize
     * @param uid
     * @return
     */
    @GetMapping({"videos"})
    public ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize, String uid) {
        PageResult result = manageService.videos(page, pagesize, uid);
        return ResponseEntity.ok(result);
    }
}
