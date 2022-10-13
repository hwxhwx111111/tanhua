package com.itheima.tanhua.vo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationVo implements Serializable {
    private Integer eachLoveCount;
    private Integer loveCount;
    private Integer fanCount;
}
