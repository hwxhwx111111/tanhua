package com.itheima.tanhua.web;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.pojo.mongo.TodayBest;
import com.itheima.tanhua.service.BaiduService;
import com.itheima.tanhua.service.QuestionService;
import com.itheima.tanhua.service.RecommendService;
import com.itheima.tanhua.service.TanhuaService;
import com.itheima.tanhua.vo.mongo.NearUserVo;
import com.itheima.tanhua.vo.mongo.PageResult;
import com.itheima.tanhua.vo.mongo.TodayBestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private BaiduService baiduService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TanhuaService   tanhuaService;



    /**
     * @description: 今日佳人
     * @author: 黄伟兴
     * @date: 2022/9/23 1:38
     * @param: [token]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/todayBest")
    public ResponseEntity<TodayBestVo> todayBest() {
        TodayBestVo todayBestVo = recommendService.todayBest();
        return ResponseEntity.ok(todayBestVo);
    }

    /**
     * @description: 佳人推荐
     * @author: 黄伟兴
     * @date: 2022/9/23 1:50
     * @param: [token, page, pagesize]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("recommendation")
    public ResponseEntity<PageResult<TodayBestVo>> recommendation(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize)
    {
        PageResult<TodayBestVo> pageResult = recommendService.recommendation(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * @description: 查看佳人详情
     * @author: 黄伟兴
     * @date: 2022/9/27 14:25
     * @param: [userId]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity<TodayBest> personalInfo(@PathVariable("id") Long userId) {
        TodayBest todayBestVo = recommendService.personalInfo(userId);
        return ResponseEntity.ok(todayBestVo);
    }

    /**
     * @description: 查看陌生人问题
     * @author: 黄伟兴
     * @date: 2022/9/27 14:02
     * @param: [userId]
     * @return: org.springframework.http.ResponseEntity
     **/
    @GetMapping("/strangerQuestions")
    public ResponseEntity<String> strangerQuestions(@RequestParam Long userId) {
        String strangerQuestions = questionService.strangerQuestions(userId);
        return ResponseEntity.ok(strangerQuestions);
    }

    /**
     * @description: 回复陌生人问题
     * @author: 黄伟兴
     * @date: 2022/9/27 14:07
     * @param: [map]
     * @return: org.springframework.http.ResponseEntity
     **/
    @PostMapping("/strangerQuestions")
    public ResponseEntity<String> replyQuestions(@RequestBody Map<String,String> map){
        Long userId = Convert.toLong(map.get("userId"));
        String reply = map.get("reply");
        questionService.replyQuestions(userId,reply);
        return ResponseEntity.ok("回复陌生人问题成功！");
    }


    /**
     * @description: 探花-左滑右滑
     * @author: 黄伟兴
     * @date: 2022/9/29 20:31
     * @param: []
     * @return: org.springframework.http.ResponseEntity
     **/
  @GetMapping("/cards")
    public ResponseEntity queryCardsList() {
        List<TodayBestVo> list = tanhuaService.queryCardsList();
        return ResponseEntity.ok(list);
    }

    /**
     * 喜欢
     */

    @GetMapping("{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id") Long likeUserId) {
        this.tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Void> notLikeUser(@PathVariable("id") Long likeUserId) {
        this.tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }




    /**
     * 更新位置
     */
    @PostMapping("/location")
    public ResponseEntity updateLocation(@RequestBody Map param) {
        Double longitude = Double.valueOf(param.get("longitude").toString());
        Double latitude = Double.valueOf(param.get("latitude").toString());
        String address = param.get("addrStr").toString();
        baiduService.updateLocation(longitude, latitude,address);
        return ResponseEntity.ok(null);
    }


    /**
     * 搜附近
     */
    @GetMapping("/search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(String gender,
                                                          @RequestParam(defaultValue = "2000") String distance) {
        List<NearUserVo> list = this.tanhuaService.queryNearUser(gender, distance);
        return ResponseEntity.ok(list);
    }

}
