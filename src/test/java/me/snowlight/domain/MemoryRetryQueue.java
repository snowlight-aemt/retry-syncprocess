package me.snowlight.domain;

import java.util.List;

class MemoryRetryQueue implements RetryQueue {
    @Override
    public void enQueue(RetryDate retryDate, int nth) {
    }

    @Override
    public List<RetryDate> deQueAll(int nth) {
        return null;
    }
}
