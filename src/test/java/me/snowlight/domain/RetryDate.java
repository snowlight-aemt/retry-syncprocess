package me.snowlight.domain;

import lombok.Getter;
import me.snowlight.domain.team.TeamDao;

import java.time.LocalDateTime;

@Getter
public class RetryDate {
    private Long id;
    private String name;
    private Integer memberCount;
    private LocalDateTime createdAt;

    private RetryDate(Long id, String name, Integer memberCount) {
        this.id = id;
        this.name = name;
        this.memberCount = memberCount;
        this.createdAt = LocalDateTime.now();
    }

    public RetryDate(TeamDao team) {
        this(team.getId(), team.getName(), team.getMemberCount());
    }
}
