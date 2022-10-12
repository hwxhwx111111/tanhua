package com.itheima.tanhua.web;


import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.service.CommentService;
import com.itheima.tanhua.service.MovementService;
import com.itheima.tanhua.vo.mongo.CommentVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//评论
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private MovementService movementService;

    @Autowired
    private CommentService commentService;

    /**
     * @description: 对动态进行评论
     * @author: 黄伟兴
     * @date: 2022/9/26 18:43
     * @param: [movementId, String]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping
    public ResponseEntity<String> comments(@RequestBody Map<String,String> map) {
        String movementId = Convert.toStr(map.get("movementId"));
        String comment = Convert.toStr(map.get("comment"));
        commentService.saveComments(movementId, comment);
        return ResponseEntity.ok("评论发布成功！");
    }

    /**
     * @description: 评论列表分页查询
     * @author: 黄伟兴
     * @date: 2022/9/26 9:12
     * @param: [page, pagesize, movementId]   根据动态id查询评论列表
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping
    public ResponseEntity<PageResult<CommentVo>> findComments(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize,
                                                              @RequestParam(value = "movementId") String movementId) {
        PageResult<CommentVo> result = commentService.findComments(page, pagesize, movementId);
        return ResponseEntity.ok(result);
    }

    /**
     * 点赞数
     * @param id
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable String id){
        Integer count=commentService.like(id);
        return ResponseEntity.ok(count);
    }

    /**
     * 取消点赞
     * @param id
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable String id){
        Integer count=commentService.dislike(id);

        return ResponseEntity.ok(count);
    }
}
