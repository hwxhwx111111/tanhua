package com.itheima.tanhua.vo.mongo;

import lombok.Data;

@Data
public class TodayBestVo {

    private Long id;
    private String nickname;
    private String avatar;
    private String[] tags;
    private String gender;
    private Integer age;
    private Integer fateValue;  //缘分值


}
