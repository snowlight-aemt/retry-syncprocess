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

    RetryQueue mockRetryQueue = Mockito.mock(RetryQueue.class);

    @Test
    void sync() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync).sync();

        SyncProcess syncProcess = new SyncProcess(mockSync, mockRetryQueue);
        SyncResult syncResult = syncProcess.run(null);
        
        assertThat(syncResult).isEqualTo(SyncResult.FAILED);
    }

    @Test
    void toRetryData() {
        SyncProcess syncProcess = new SyncProcess(null, mockRetryQueue);
        TeamDao team = new TeamDao(1L, "TeamA", 12);
        RetryDate retryData = syncProcess.toRetryData(team);

        assertThat(retryData).isNotNull();
        assertThat(retryData.getId()).isEqualTo(team.getId());
        assertThat(retryData.getName()).isEqualTo(team.getName());
        assertThat(retryData.getMemberCount()).isEqualTo(team.getMemberCount());
        assertThat(retryData.getCreatedAt()).isNotNull();
    }

    @Test
    void enQueue() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync).sync();

        SyncProcess syncProcess = new SyncProcess(mockSync, mockRetryQueue);
        SyncResult syncResult = syncProcess.run(null);

        assertThat(syncResult).isEqualTo(SyncResult.FAILED);
        BDDMockito
                .verify(mockRetryQueue)
                .enQueue(Mockito.any(RetryDate.class), Mockito.anyInt());
    }

    private class SyncProcess {

        private final Sync sync;
        private final RetryQueue mockRetryQueue;

        public SyncProcess(Sync sync, RetryQueue mockRetryQueue) {
            this.sync = sync;
            this.mockRetryQueue = mockRetryQueue;
        }

        public SyncResult run(TeamDao teamA) {
            try {
                this.sync.sync();
            } catch (RuntimeException e) {
                RetryDate retryDate = toRetryData(new TeamDao(1L, "test", 12));
                this.mockRetryQueue.enQueue(retryDate, 1);
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

    private class RetryQueue {
        public void enQueue(RetryDate retryDate, int nth) {
        }
    }
}