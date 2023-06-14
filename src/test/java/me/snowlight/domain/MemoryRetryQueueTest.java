package me.snowlight.domain;

import me.snowlight.domain.queue.MemoryRetryQueue;
import me.snowlight.domain.queue.RetryData;
import me.snowlight.domain.queue.RetryQueue;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryRetryQueueTest {
    @Test
    void enQueueAndDequeAll() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        RetryData retryData1 = new RetryData(new TeamDao(1L, "TeamC", 22));
        retryQueue.enQueue(retryData1, 1);
        RetryData retryData2 = new RetryData(new TeamDao(2L, "TeamC", 22));
        retryQueue.enQueue(retryData2, 1);
        RetryData retryData3 = new RetryData(new TeamDao(3L, "TeamC", 22));
        retryQueue.enQueue(retryData3, 1);

        List<RetryData> retryData = retryQueue.deQueAll(1);

        assertThat(retryData).hasSize(3);
        assertThat(retryData).contains(retryData1, retryData2, retryData3);
    }

    @Test
    void enQueueAndDequeAll2() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        RetryData retryData1 = new RetryData(new TeamDao(1L, "TeamC", 22));
        retryQueue.enQueue(retryData1, 2);
        RetryData retryData2 = new RetryData(new TeamDao(2L, "TeamC", 22));
        retryQueue.enQueue(retryData2, 2);
        RetryData retryData3 = new RetryData(new TeamDao(3L, "TeamC", 22));
        retryQueue.enQueue(retryData3, 2);

        List<RetryData> retryData = retryQueue.deQueAll(2);

        assertThat(retryData).hasSize(3);
        assertThat(retryData).contains(retryData1, retryData2, retryData3);
    }

    @Test
    void dequeAll_delete_previous_queue() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        RetryData retryData1 = new RetryData(new TeamDao(1L, "TeamC", 22));
        retryQueue.enQueue(retryData1, 2);
        RetryData retryData2 = new RetryData(new TeamDao(2L, "TeamC", 22));
        retryQueue.enQueue(retryData2, 2);
        RetryData retryData3 = new RetryData(new TeamDao(3L, "TeamC", 22));
        retryQueue.enQueue(retryData3, 2);

        retryQueue.deQueAll(2);
        List<RetryData> retryData = retryQueue.deQueAll(2);

        assertThat(retryData).hasSize(0);
        assertThat(retryData).isEmpty();
    }

    @Test
    void dequeAll_case_empty() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        List<RetryData> retryData = retryQueue.deQueAll(1);

        assertThat(retryData).isNotNull();
        assertThat(retryData).hasSize(0);
    }
}
