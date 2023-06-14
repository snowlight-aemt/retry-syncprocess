package me.snowlight.domain.sync;

import me.snowlight.domain.queue.RetryData;
import me.snowlight.domain.queue.RetryQueue;
import me.snowlight.domain.team.TeamDao;

import java.util.List;

public class SyncRetry {
    private Sync sync;
    private RetryQueue mockRetryQueue;

    public SyncRetry(Sync sync, RetryQueue mockRetryQueue) {
        this.sync = sync;
        this.mockRetryQueue = mockRetryQueue;
    }

    public void retrySync(int nth) {
        List<RetryData> retryDataList = this.mockRetryQueue.deQueAll(nth);
        for (RetryData retryData : retryDataList) {
            try {
                this.sync.sync(new TeamDao(retryData.getId(), retryData.getName(), retryData.getMemberCount()));
            } catch (RuntimeException e) {
                this.mockRetryQueue.enQueue(retryData, nth + 1);
            }
        }
    }
}
