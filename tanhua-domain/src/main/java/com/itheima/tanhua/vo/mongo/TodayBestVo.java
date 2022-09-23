package com.itheima.tanhua.vo.mongo;

import cn.hutool.core.convert.Convert;
import com.itheima.tanhua.pojo.db.UserInfo;
import com.itheima.tanhua.pojo.mongo.RecommendUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
public class TodayBestVo implements Serializable {

    private Long id;
    private String nickname;
    private String avatar;
    private String[] tags;
    private String gender;
    private Integer age;
    private Integer fateValue;  //缘分值


    /**
     * 在vo对象中，补充一个工具方法，封装转化过程
     */
    public static TodayBestVo init(UserInfo userInfo, RecommendUser recommendUser) {
        TodayBestVo vo = new TodayBestVo();
        BeanUtils.copyProperties(userInfo,vo);
        if(userInfo.getTags() != null) {
            vo.setTags(userInfo.getTags().split(","));
        }
        vo.setFateValue(Convert.toInt(recommendUser.getScore()));
        return vo;
    }

}
