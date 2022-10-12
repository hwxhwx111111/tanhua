package com.itheima.tanhua.vo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApifoxModelVo implements Serializable {
    private long fateValue;
    private String friendAvatar;
    private String myAvatar;
    private List<String> tags;
}
