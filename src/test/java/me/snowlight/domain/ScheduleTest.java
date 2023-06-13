package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScheduleTest {
    @Test
    void run() {
        MemoryRetryQueue retryQueue = new MemoryRetryQueue();
        retryQueue.enQueue(new RetryDate(new TeamDao(1L, "team1", 2)), 1);

        retryQueue.enQueue(new RetryDate(new TeamDao(2L, "team1", 3)), 2);
        retryQueue.enQueue(new RetryDate(new TeamDao(3L, "team1", 4)), 2);

        retryQueue.enQueue(new RetryDate(new TeamDao(4L, "team1", 5)), 3);
        retryQueue.enQueue(new RetryDate(new TeamDao(5L, "team1", 6)), 3);
        retryQueue.enQueue(new RetryDate(new TeamDao(6L, "team1", 7)), 3);

        Schedule schedule = new Schedule(new SyncRetry(new Sync(), retryQueue));

        schedule.run(5000, 10000, 15000);

        Assertions.assertThat(retryQueue.deQueAll(1)).hasSize(0);
        Assertions.assertThat(retryQueue.deQueAll(2)).hasSize(0);
        Assertions.assertThat(retryQueue.deQueAll(3)).hasSize(0);
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
