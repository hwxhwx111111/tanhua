package com.itheima.tanhua.web;


import com.itheima.tanhua.dto.mongo.MovementDto;
import com.itheima.tanhua.service.CommentService;
import com.itheima.tanhua.service.MovementService;
import com.itheima.tanhua.vo.mongo.MovementsVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.VisitorsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/movements")
public class MovementController {

    @Autowired
    private MovementService movementService;

    @Autowired
    private CommentService commentService;

    /**
     * @description: 发布动态
     * @author: 黄伟兴
     * @date: 2022/9/24 1:03
     * @param: [movementDto, imageContent]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping
    public ResponseEntity<String> publish(MovementDto movementDto, MultipartFile[] imageContent) {
        movementService.publish(movementDto, imageContent);
        return ResponseEntity.ok("动态发布成功！");
    }

    /**
     * @description: 根据id分页查询个人动态
     * @author: 黄伟兴
     * @date: 2022/9/24 9:38
     * @param: [userId, page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/all")
    public ResponseEntity<PageResult<MovementsVo>> findMovementById(
            @RequestParam Long userId,
            @RequestParam(value = "page", defaultValue = "1")
                    Integer page, @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize) {
        PageResult<MovementsVo> result = movementService.findMovementById(userId, page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 查询单条动态
     * @author: 黄伟兴
     * @date: 2022/9/25 20:32
     * @param: [id]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}")
    public ResponseEntity<MovementsVo> findRecommendById(@PathVariable("id") String movementId) {
        MovementsVo result = movementService.findRecommendById(movementId);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 查询好友动态列表
     * @author: 黄伟兴
     * @date: 2022/9/24 10:45
     * @param: [page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping
    public ResponseEntity<PageResult<MovementsVo>> findFriendMovements(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize) {
        PageResult<MovementsVo> result = movementService.findFriendMovements(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 查询推荐动态
     * @author: 黄伟兴
     * @date: 2022/9/25 17:54
     * @param: [page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/recommend")
    public ResponseEntity<PageResult<MovementsVo>> recommend(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize) {
        PageResult<MovementsVo> result = movementService.recommend(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * @description: 对动态进行点赞，根据动态的id
     * @author: 黄伟兴
     * @date: 2022/9/26 10:27
     * @param: [movementId]   动态的id
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}/like")
    public ResponseEntity<Integer> like(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.like(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * @description: 取消点赞，根据动态id
     * @author: 黄伟兴
     * @date: 2022/9/26 20:40
     * @param: [movementId] 动态id
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}/dislike")
    public ResponseEntity< Integer> disLike(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.disLike(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * @description: 对动态进行喜欢，根据动态的id
     * @author: 黄伟兴
     * @date: 2022/9/26 10:27
     * @param: [movementId]   动态的id
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}/love")
    public ResponseEntity<Integer> love(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.love(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * @description: 取消喜欢，根据动态id
     * @author: 黄伟兴
     * @date: 2022/9/26 20:40
     * @param: [movementId] 动态id
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}/unlove")
    public ResponseEntity<Integer>  unLove(@PathVariable("id") String movementId) {
        Integer likeCount = commentService.unLove(movementId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * @description: 谁看过我
     * @author: 黄伟兴
     * @date: 2022/10/6 15:15
     * @param: []
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("visitors")
    public ResponseEntity queryVisitorsList(){
        List<VisitorsVo> list = movementService.queryVisitorsList();
        return ResponseEntity.ok(list);
    }
}
