package com.itheima.tanhua.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.tanhua.pojo.db.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
