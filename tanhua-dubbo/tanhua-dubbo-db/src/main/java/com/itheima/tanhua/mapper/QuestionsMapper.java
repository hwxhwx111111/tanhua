package com.itheima.tanhua.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.tanhua.pojo.db.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionsMapper extends BaseMapper<Question> {
}
