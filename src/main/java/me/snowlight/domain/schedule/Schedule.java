package me.snowlight.domain.schedule;

import me.snowlight.domain.sync.SyncRetry;

public class Schedule {
    private final SyncRetry syncRetry;

    public Schedule(SyncRetry syncRetry) {
        this.syncRetry = syncRetry;
    }

    public void run(int... repeat) {
        int idx = 1;
        for (int time : repeat) {
            try {
                Thread.sleep(time);
                System.out.println(idx + "  ");
                this.syncRetry.retrySync(idx++);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
