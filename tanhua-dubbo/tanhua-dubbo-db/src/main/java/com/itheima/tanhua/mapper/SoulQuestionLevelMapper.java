package com.itheima.tanhua.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.tanhua.pojo.db.SoulQuestionLevel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SoulQuestionLevelMapper extends BaseMapper<SoulQuestionLevel> {
    List<SoulQuestionLevel> get();
}
