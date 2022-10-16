package com.itheima.tanhua.mapper;

import com.itheima.tanhua.pojo.db.SoulQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SoulQuestionMapper {
    List<SoulQuestion> get(String levelId);

    @Select("select level_id from tb_soul_question where id = #{questionId}")
    String getLevelId(String questionId);
}
