package com.itheima.tanhua.mapper;

import com.itheima.tanhua.pojo.db.SoulOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SoulOptionMapper {

    List<SoulOption> get(String questionId);

    Integer calculateScore(List<String> optionIds);
}
