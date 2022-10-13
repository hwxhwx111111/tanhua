package com.tanhua.admin.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.tanhua.pojo.db.Analysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface AnalysisMapper extends BaseMapper<Analysis> {
    @Select("select ${type} from tb_analysis_by_day where record_date between #{start} and #{end}")
    List<Integer> findUserCount(@Param("start") DateTime start, @Param("end") DateTime end, @Param("type") String type);

    @Select("select ${type} ,record_date from tb_analysis_by_day where record_date between #{start} and #{end}")
    List<Analysis> findUsers(@Param("start") DateTime start, @Param("end") DateTime end, @Param("type") String type);
}
