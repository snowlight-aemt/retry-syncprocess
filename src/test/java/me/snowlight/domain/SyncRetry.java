package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;

import java.util.List;

class SyncRetry {
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
