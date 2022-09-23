package com.itheima.tanhua.web;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.service.FriendService;
import com.itheima.tanhua.service.MessagesService;
import com.itheima.tanhua.vo.db.UserInfoVo;
import com.itheima.tanhua.vo.mongo.ContactVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;


    @Autowired
    private FriendService friendService;

    /**
     * @description: 根据hxId查询用户信息
     * @author: 黄伟兴
     * @date: 2022/9/28 23:35
     * @param: [huanxinId]
     * @return: org.springframework.http.ResponseEntity<com.itheima.tanhua.vo.db.UserInfoVo>
     **/
    @GetMapping("/userinfo")
    public ResponseEntity<UserInfoVo> findUserInfoByHuanxinId(String huanxinId){
      UserInfoVo userInfoVo =  messagesService.findUserInfoByHuanxinId(huanxinId);
      return ResponseEntity.ok(userInfoVo);
    }

    /**
     * @description: 添加好友
     * @author: 黄伟兴
     * @date: 2022/9/27 14:58
     * @param: [map]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("/contacts")
    public ResponseEntity<String> saveContacts(@RequestBody Map<String,String> map){
        //好友id
        Long friendId = Convert.toLong(map.get("userId"));

        messagesService.saveContacts(friendId);
        return ResponseEntity.ok("好友添加成功！");
    }

    /**
     * @description: 联系人列表分页查询
     * @author: 黄伟兴
     * @date: 2022/9/27 15:26
     * @param: [page, pagesize, keyword]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/contacts")
    public ResponseEntity<PageResult<ContactVo>> findContacts(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                       @RequestParam(value = "pagesize",defaultValue = "10") Integer pagesize,
                                       @RequestParam(defaultValue = "1") String keyword){
       PageResult<ContactVo> result =  messagesService.findContacts(page,pagesize,keyword);
       return ResponseEntity.ok(result);
    }
}
