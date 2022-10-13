package com.itheima.tanhua.web;

import com.itheima.tanhua.service.*;


import com.itheima.tanhua.vo.db.PageResult;
import com.itheima.tanhua.vo.db.SettingsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UsersController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private LikeUserService likeUserService;
    @Autowired
    private SettingsService settingsService;

    /**
     * 设置陌生人问题
     *
     * @param param
     * @return
     */
    @PostMapping("questions")
    public ResponseEntity questions(@RequestBody Map<String, String> param) {
        String content = param.get("content");
        questionService.setQuestion(content);
        return ResponseEntity.ok(null);
    }


    /**
     * 设置通知
     *
     * @param params
     * @return
     */
    @PostMapping("notifications/setting")
    public ResponseEntity notificationsSet(@RequestBody Map params) {
        boolean likeNotification = (boolean) params.get("likeNotification");
        boolean pinglunNotification = (boolean) params.get("pinglunNotification");
        boolean gonggaoNotification = (boolean) params.get("gonggaoNotification");
        notificationsService.notificationsSet(likeNotification, pinglunNotification, gonggaoNotification);
        return ResponseEntity.ok(null);
    }

    /**
     * 查看黑名单人列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("blacklist")
    public ResponseEntity blacklist(@RequestParam(value = "page", defaultValue = "1", required = false) String page
            , @RequestParam(value = "pagsize", defaultValue = "10", required = false) String pagesize) {
        PageResult pageResult = blacklistService.findBlacklist(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 移除黑名单
     *
     * @return
     */
    @DeleteMapping("blacklist/{uid}")
    public ResponseEntity deleteblacklist(@PathVariable String uid) {
        blacklistService.deleteblacklist(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 喜欢粉丝
     * @param uid
     * @return
     */
    //喜欢粉丝以后,两个人成为好友
    @PostMapping("blacklist/{uid}")
    public ResponseEntity fansLike(@PathVariable String uid) {
        likeUserService.fansLike(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询通用设置
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity settings() {
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }
}
