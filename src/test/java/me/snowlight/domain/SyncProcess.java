package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;

class SyncProcess {

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
