package com.itheima.tanhua.web;


import com.itheima.tanhua.service.PeachBlossomService;
import com.itheima.tanhua.vo.mongo.PeachBlossomVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/peachblossom")
public class PeachBlossomController {

    @Autowired
    private PeachBlossomService peachBlossomService;

    /**
     * 发送桃花传音
     */
    @PostMapping
    public ResponseEntity<?> send(MultipartFile soundFile){
        peachBlossomService.send(soundFile);
        return ResponseEntity.ok(null);
    }


    /**
     * 接收桃花传音
     */
    @GetMapping
    public ResponseEntity<PeachBlossomVo> recive(){
        PeachBlossomVo peachBlossomVo = peachBlossomService.recive();
        return ResponseEntity.ok(peachBlossomVo);
    }
}
