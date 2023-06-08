package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;

public class ScheduleTest {
    @Test
    void name() {
        MemoryRetryQueue mockRetryQueue = new MemoryRetryQueue();
        mockRetryQueue.enQueue(new RetryDate(new TeamDao(1L, "team1", 2)), 1);

        mockRetryQueue.enQueue(new RetryDate(new TeamDao(2L, "team1", 3)), 2);
        mockRetryQueue.enQueue(new RetryDate(new TeamDao(3L, "team1", 4)), 2);

        mockRetryQueue.enQueue(new RetryDate(new TeamDao(4L, "team1", 5)), 3);
        mockRetryQueue.enQueue(new RetryDate(new TeamDao(5L, "team1", 6)), 3);
        mockRetryQueue.enQueue(new RetryDate(new TeamDao(6L, "team1", 7)), 3);

        Schedule schedule = new Schedule(new SyncRetry(new Sync(), mockRetryQueue));

        schedule.run(20000, 40000, 60000);

    }

    private class Schedule {
        private final SyncRetry syncRetry;

        public Schedule(SyncRetry syncRetry) {
            this.syncRetry = syncRetry;
        }

        public void run(int ...repeat) {
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
}
