package com.itheima.tanhua.pojo.db;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
//公告列表
public class AnnouncementVo implements Serializable {
    private String id;
    private String title;
    private String description;
    private String createDate;

}
