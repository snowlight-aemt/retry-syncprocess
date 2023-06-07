package me.snowlight.domain;

import lombok.Getter;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SyncProcessTest {
    Sync mockSync = Mockito.mock(Sync.class);

    MemoryRetryQueue mockMemoryRetryQueue = Mockito.mock(MemoryRetryQueue.class);

    RetryQueueStub retryQueueStub = new RetryQueueStub();
    @Test
    void sync() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync).sync(Mockito.any(TeamDao.class));

        SyncProcess syncProcess = new SyncProcess(mockSync, mockMemoryRetryQueue);
        SyncResult syncResult = syncProcess.run(Mockito.mock(TeamDao.class));
        
        assertThat(syncResult).isEqualTo(SyncResult.FAILED);
    }

    @Test
    void toRetryData() {
        SyncProcess syncProcess = new SyncProcess(null, mockMemoryRetryQueue);
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
                .given(mockSync).sync(Mockito.any(TeamDao.class));

        SyncProcess syncProcess = new SyncProcess(mockSync, mockMemoryRetryQueue);
        SyncResult syncResult = syncProcess.run(Mockito.mock(TeamDao.class));

        assertThat(syncResult).isEqualTo(SyncResult.FAILED);
        BDDMockito
                .verify(mockMemoryRetryQueue)
                .enQueue(Mockito.any(RetryDate.class), Mockito.anyInt());
    }

    @Test
    void enQueue_with_RetryQueueStub() {
        TeamDao testB = new TeamDao(1L, "TestB", 15);
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync).sync(testB);


        SyncProcess syncProcess = new SyncProcess(mockSync, this.retryQueueStub);
        SyncResult syncResult = syncProcess.run(testB);


        List<RetryDate> retryDates = this.retryQueueStub.deQueAll(1);
        assertThat(syncResult).isEqualTo(SyncResult.FAILED);
        assertThat(retryDates.get(0).getId()).isEqualTo(testB.getId());
        assertThat(retryDates.get(0).getName()).isEqualTo(testB.getName());
        assertThat(retryDates.get(0).getMemberCount()).isEqualTo(testB.getMemberCount());
    }

    private class SyncProcess {

        private final Sync sync;
        private final RetryQueue retryQueue;

        public SyncProcess(Sync sync, RetryQueue mockMemoryRetryQueue) {
            this.sync = sync;
            this.retryQueue = mockMemoryRetryQueue;
        }

        public SyncResult run(TeamDao team) {
            try {
                this.sync.sync(team);
            } catch (RuntimeException e) {
                RetryDate retryDate = toRetryData(team);
                this.retryQueue.enQueue(retryDate, 1);
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
        public void sync(TeamDao teamDao) {
        }
    }

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

    private class MemoryRetryQueue implements RetryQueue {
        @Override
        public void enQueue(RetryDate retryDate, int nth) {
        }

        @Override
        public List<RetryDate> deQueAll(int nth) {
            return null;
        }
    }

    private class RetryQueueStub implements RetryQueue {

        private RetryDate retryDate;
        private int nth;

        @Override
        public void enQueue(RetryDate retryDate, int nth) {
            this.retryDate = retryDate;
            this.nth = nth;
        }

        @Override
        public List<RetryDate> deQueAll(int nth) {
            if (this.nth == nth) {
                return List.of(this.retryDate);
            }

            return Collections.emptyList();
        }
    }
}