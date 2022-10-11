package com.itheima.tanhua.dto.db;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreezeDto implements Serializable {
    //用户id
    private Integer userId;

    //冻结时间，1为冻结3天，2为冻结7天，3为永久冻结
    private Integer freezingTime;

    //冻结范围，1为冻结登录，2为冻结发言，3为冻结发布动态
    private Integer freezingRange;

    //冻结原因
    private String reasonsForFreezing;

    //冻结备注
    private String frozenRemarks;
}
