package me.snowlight.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.snowlight.domain.team.TeamDao;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RetryDate {
    private Long id;
    private String name;
    private Integer memberCount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
