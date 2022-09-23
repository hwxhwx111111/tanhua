package com.itheima.tanhua.pojo.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends BasePojo {

    @TableId(type = IdType.INPUT)
    private Long id;
    private String nickname;
    private String avatar;
    private String tags;
    private String gender;
    private int age;
    private String education;
    private String city;
    private String birthday;
    private String coverPic;
    private String profession;
    private String income;
    private int marriage;
}
