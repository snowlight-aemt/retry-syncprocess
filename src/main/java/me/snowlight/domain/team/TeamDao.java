package me.snowlight.domain.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamDao {
    private Long id;
    private String name;
    private Integer memberCount;
}
