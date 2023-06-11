package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

public class SyncRetryTest {
    Sync mockSync = Mockito.mock(Sync.class);

    @Test
    void retrySync() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(this.mockSync).sync(Mockito.any(TeamDao.class));

        RetryQueue retryQueue = new RetryQueueStub();
        retryQueue.enQueue(new RetryDate(new TeamDao(1L, "teamA", 14)), 1);
        retryQueue.enQueue(new RetryDate(new TeamDao(2L, "teamB", 14)), 1);

        SyncRetry syncRetry = new SyncRetry(this.mockSync, retryQueue);
        syncRetry.retrySync(1);

        Assertions.assertThat(retryQueue.deQueAll(2)).hasSize(2);
    }

    @Test
    void retrySync_previous() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(this.mockSync).sync(Mockito.any(TeamDao.class));

        RetryQueue retryQueue = new RetryQueueStub();
        retryQueue.enQueue(new RetryDate(new TeamDao(1L, "teamA", 14)), 1);
        retryQueue.enQueue(new RetryDate(new TeamDao(2L, "teamB", 14)), 1);

        SyncRetry syncRetry = new SyncRetry(this.mockSync, retryQueue);
        syncRetry.retrySync(1);

        Assertions.assertThat(retryQueue.deQueAll(1)).isEmpty();
    }

}
