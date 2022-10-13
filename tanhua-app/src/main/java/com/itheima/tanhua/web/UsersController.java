package com.itheima.tanhua.web;

import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.service.*;


import com.itheima.tanhua.vo.db.*;
import com.itheima.tanhua.vo.mongo.RelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Relation;
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
    @Autowired
    private UserService userService;
    @Autowired
    private TanhuaService tanhuaService;

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
     *
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
     *
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity settings() {
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }

    /**
     * 发送手机验证码
     *
     * @return
     */
    @PostMapping("phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode() {
        userService.sendMsg();
        return ResponseEntity.ok(null);
    }

    /**
     * 检查验证码
     *
     * @param verificationCode
     * @return
     */
    @PostMapping("phone/checkVerificationCode")
//    public ResponseEntity checkVerificationCode(@RequestParam String verificationCode) {
    public ResponseEntity checkVerificationCode(@RequestBody Map verificationCode) {
        String o = (String)verificationCode.get("verificationCode");
        Boolean bo = userService.jcVerificationCode(o);
        return ResponseEntity.ok(bo);
    }

    /**
     * 保存手机号
     *
     * @param phone
     * @return
     */
    @PostMapping("phone")
    public ResponseEntity savePhone(@RequestBody Map<String, String> phone) {
        String phone1 = phone.get("phone");
        userService.savePhone(phone1);
        return ResponseEntity.ok(null);
    }

    /**
     * 取消喜欢
     * @param likeUserId
     * @return
     */
    @DeleteMapping("like/{uid}")
    public ResponseEntity cancelLove(@PathVariable("uid") Long likeUserId) {
        tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询用户好友,喜欢,粉丝数量
     * @return
     */
    @GetMapping("counts")
    public ResponseEntity findCounts() {
        RelationVo user = userService.findCounts();
        return ResponseEntity.ok(user);
    }

    /**
     * 查询用户好友,喜欢,粉丝,看我的人详细信息
     * @param page
     * @param pagesize
     * @param type
     * @return
     */
    @GetMapping("friends/{type}")
    public ResponseEntity friends(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize, @PathVariable("type") Long type) {
        PageResultM<UserInfoCVo> user = userService.friends(page, pagesize, type);
        return ResponseEntity.ok(user);
    }

    /**
     *读取用户资料
     * @return
     */
    @GetMapping
    public ResponseEntity findVideoList() {
        UserInfoBVo user = userService.findById();
        return ResponseEntity.ok(user);
    }

    /**
     *保存用户资料
     * @param userInfo
     * @return
     */
    @PutMapping
    public ResponseEntity saveVideoList(@RequestBody UserInfo userInfo) {
        userService.saveById(userInfo);
        return ResponseEntity.ok("保存成功");
    }

    /**
     * 修改用户头像
     * @param headPhoto
     * @return
     */
    @PostMapping("header")
    public ResponseEntity updateHeadPhoto(MultipartFile headPhoto) {
        userService.updateHeadPhoto(headPhoto);
        return ResponseEntity.ok("更改成功");
    }

}
