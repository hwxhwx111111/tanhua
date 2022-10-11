package com.itheima.tanhua.vo.db;

import com.itheima.tanhua.pojo.db.UserInfo;
import lombok.Data;

@Data
public class UsersInfoVo extends UserInfo {
    private String userStatus = "1";
}
