package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.List;

public class SyncRetryTest {
    Sync mockSync = Mockito.mock(Sync.class);
//    RetryQueue mockRetryQueue = Mockito.mock(RetryQueue.class);

    @Test
    void retrySync() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(this.mockSync).sync(Mockito.any(TeamDao.class));
//        BDDMockito
//                .given(this.mockRetryQueue.deQueAll(1))
//                .willReturn(List.of(new RetryDate(new TeamDao(1L, "teamA", 14))));

        RetryQueue retryQueue = new RetryQueueStub();
        retryQueue.enQueue(new RetryDate(new TeamDao(1L, "teamA", 14)), 1);
        retryQueue.enQueue(new RetryDate(new TeamDao(2L, "teamB", 14)), 1);

        SyncRetry syncRetry = new SyncRetry(this.mockSync, retryQueue);
        syncRetry.retrySync(1);

        Assertions.assertThat(retryQueue.deQueAll(2)).hasSize(2);

//        BDDMockito
//                .verify(this.mockRetryQueue)
//                .enQueue(Mockito.any(RetryDate.class), Mockito.anyInt());
    }

    private class SyncRetry {
        private Sync sync;
        private RetryQueue mockRetryQueue;

        public SyncRetry(Sync sync, RetryQueue mockRetryQueue) {
            this.sync = sync;
            this.mockRetryQueue = mockRetryQueue;
        }

        public void retrySync(int nth) {
            List<RetryDate> retryDates = this.mockRetryQueue.deQueAll(nth);
            for (RetryDate retryDate : retryDates) {
                try {
                    this.sync.sync(new TeamDao(retryDate.getId(), retryDate.getName(), retryDate.getMemberCount()));
                } catch (RuntimeException e) {
                    this.mockRetryQueue.enQueue(retryDate, nth + 1);
                }
            }
        }
    }
}
