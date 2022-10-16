package com.itheima.tanhua.vo.mongo;


import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.PeachBlossom;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 袁鹏
 * @date 2022-10-11-15:00
 */
@Data
public class PeachBlossomVo implements Serializable {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String soundUrl;
    private Integer remainingTimes;

    public static PeachBlossomVo init(UserInfo userInfo, PeachBlossom peachBlossom, Integer remainingTimes){
       PeachBlossomVo peachBlossomVo = new PeachBlossomVo();
       peachBlossomVo.setId(userInfo.getId());
       peachBlossomVo.setAvatar(userInfo.getAvatar());
       peachBlossomVo.setNickname(userInfo.getNickname());
       peachBlossomVo.setGender(userInfo.getGender());
       peachBlossomVo.setAge(userInfo.getAge());
       peachBlossomVo.setSoundUrl(peachBlossom.getUrl());
       peachBlossomVo.setRemainingTimes(remainingTimes);
       return peachBlossomVo;
    }
}
