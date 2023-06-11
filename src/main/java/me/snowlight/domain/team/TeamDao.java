package me.snowlight.domain.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TeamDao {
    private Long id;
    private String name;
    private Integer memberCount;
}
