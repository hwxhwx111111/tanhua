package com.itheima.tanhua.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.tanhua.pojo.db.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
