package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryRetryQueueTest {
    @Test
    void enQueueAndDequeAll() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        RetryDate retryDate1 = new RetryDate(new TeamDao(1L, "TeamC", 22));
        retryQueue.enQueue(retryDate1, 1);
        RetryDate retryDate2 = new RetryDate(new TeamDao(2L, "TeamC", 22));
        retryQueue.enQueue(retryDate2, 1);
        RetryDate retryDate3 = new RetryDate(new TeamDao(3L, "TeamC", 22));
        retryQueue.enQueue(retryDate3, 1);

        List<RetryDate> retryDates = retryQueue.deQueAll(1);

        assertThat(retryDates).hasSize(3);
        assertThat(retryDates).contains(retryDate1, retryDate2, retryDate3);
    }

    @Test
    void enQueueAndDequeAll2() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        RetryDate retryDate1 = new RetryDate(new TeamDao(1L, "TeamC", 22));
        retryQueue.enQueue(retryDate1, 2);
        RetryDate retryDate2 = new RetryDate(new TeamDao(2L, "TeamC", 22));
        retryQueue.enQueue(retryDate2, 2);
        RetryDate retryDate3 = new RetryDate(new TeamDao(3L, "TeamC", 22));
        retryQueue.enQueue(retryDate3, 2);

        List<RetryDate> retryDates = retryQueue.deQueAll(2);

        assertThat(retryDates).hasSize(3);
        assertThat(retryDates).contains(retryDate1, retryDate2, retryDate3);
    }

    @Test
    void dequeAll_delete_previous_queue() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        RetryDate retryDate1 = new RetryDate(new TeamDao(1L, "TeamC", 22));
        retryQueue.enQueue(retryDate1, 2);
        RetryDate retryDate2 = new RetryDate(new TeamDao(2L, "TeamC", 22));
        retryQueue.enQueue(retryDate2, 2);
        RetryDate retryDate3 = new RetryDate(new TeamDao(3L, "TeamC", 22));
        retryQueue.enQueue(retryDate3, 2);

        retryQueue.deQueAll(2);
        List<RetryDate> retryDates = retryQueue.deQueAll(2);

        assertThat(retryDates).hasSize(0);
        assertThat(retryDates).isEmpty();
    }

    @Test
    void dequeAll_case_empty() {
        RetryQueue retryQueue = new MemoryRetryQueue();

        List<RetryDate> retryDates = retryQueue.deQueAll(1);

        assertThat(retryDates).isNotNull();
        assertThat(retryDates).hasSize(0);
    }
}
