package me.snowlight.domain.queue;

import java.util.List;

public interface RetryQueue {
    void enQueue(RetryData retryData, int nth);
    List<RetryData> deQueAll(int nth);
}
