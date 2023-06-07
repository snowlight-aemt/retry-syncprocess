package me.snowlight.domain;

import lombok.Getter;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class SyncProcessTest {
    Sync mockSync = Mockito.mock(Sync.class);

    @Test
    void sync() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync).sync();

        SyncProcess syncProcess = new SyncProcess(mockSync);
        SyncResult syncResult = syncProcess.run();
        
        assertThat(syncResult).isEqualTo(SyncResult.FAILED);
    }

    @Test
    void toRetryData() {
        SyncProcess syncProcess = new SyncProcess(null);
        TeamDao team = new TeamDao(1L, "TeamA", 12);
        RetryDate retryData = syncProcess.toRetryData(team);

        assertThat(retryData).isNotNull();
        assertThat(retryData.getId()).isEqualTo(team.getId());
        assertThat(retryData.getName()).isEqualTo(team.getName());
        assertThat(retryData.getMemberCount()).isEqualTo(team.getMemberCount());
        assertThat(retryData.getCreatedAt()).isNotNull();
    }

    private class SyncProcess {

        private final Sync sync;

        public SyncProcess(Sync sync) {
            this.sync = sync;
        }

        public SyncResult run() {
            try {
                this.sync.sync();
            } catch (RuntimeException e) {
                return SyncResult.FAILED;
            }

            return SyncResult.SUCCESS;
        }

        private RetryDate toRetryData(TeamDao team) {
            return new RetryDate(team);
        }
    }

    private enum SyncResult {
        SUCCESS, FAILED
    }

    private class Sync {
        public void sync() {
        }
    }

    @Getter
    private class RetryDate {
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
}