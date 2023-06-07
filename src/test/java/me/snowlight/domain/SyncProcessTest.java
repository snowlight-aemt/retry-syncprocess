package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

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
        RetryDate retryData = new RetryDate(team);

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

}