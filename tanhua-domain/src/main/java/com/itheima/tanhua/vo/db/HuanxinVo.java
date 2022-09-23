package com.itheima.tanhua.vo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuanxinVo implements Serializable {

    private String username;
    private String password;
}
