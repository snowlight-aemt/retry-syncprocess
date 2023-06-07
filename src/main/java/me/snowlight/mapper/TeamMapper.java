package me.snowlight.mapper;

import me.snowlight.domain.team.TeamDao;
import org.apache.ibatis.annotations.Select;

public interface TeamMapper {
    @Select("select * from team where id = #{id}")
    TeamDao findById(Long id);
}
