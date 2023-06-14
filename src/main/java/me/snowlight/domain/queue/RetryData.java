package me.snowlight.domain.queue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snowlight.domain.team.TeamDao;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RetryData {
    private Long id;
    private String name;
    private Integer memberCount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    private RetryData(Long id, String name, Integer memberCount) {
        this.id = id;
        this.name = name;
        this.memberCount = memberCount;
        this.createdAt = LocalDateTime.now();
    }

    public RetryData(TeamDao team) {
        this(team.getId(), team.getName(), team.getMemberCount());
    }
}
