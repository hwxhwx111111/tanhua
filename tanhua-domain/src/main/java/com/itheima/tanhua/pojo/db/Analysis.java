package com.itheima.tanhua.pojo.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tb_analysis_by_day")
public class Analysis extends BasePojo implements Serializable {

    private Long id;
    /**
     * 日期
     */
    private Date recordDate;
    /**
     * 新注册用户数
     */
    private Integer numRegistered = 0;
    /**
     * 活跃用户数
     */
    private Integer numActive = 0;
    /**
     * 登陆次数
     */
    private Integer numLogin = 0;
    /**
     * 次日留存用户数
     */
    private Integer numRetention1d = 0;


}