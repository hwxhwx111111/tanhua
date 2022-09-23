package com.itheima.tanhua.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//发布动态前端传入的文本参数
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementDto implements Serializable {

    private String textContent;  //文字
    private String longitude;   //经度
    private String latitude;    //维度
    private String location;    //位置名称

}
