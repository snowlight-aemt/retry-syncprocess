package me.snowlight.mapper;

import me.snowlight.domain.team.Team;
import org.apache.ibatis.annotations.Select;

public interface TeamMapper {
    @Select("select * from team where id = #{id}")
    Team findById(Long id);
}
