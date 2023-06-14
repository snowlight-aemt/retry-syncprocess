package me.snowlight.domain.sync;

import me.snowlight.domain.queue.RetryData;
import me.snowlight.domain.queue.RetryQueue;
import me.snowlight.domain.team.TeamDao;

public class SyncProcess {

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
            RetryData retryData = toRetryData(team);
            this.retryQueue.enQueue(retryData, 1);
            return SyncResult.FAILED;
        }

        return SyncResult.SUCCESS;
    }

    private RetryData toRetryData(TeamDao team) {
        return new RetryData(team);
    }
}
