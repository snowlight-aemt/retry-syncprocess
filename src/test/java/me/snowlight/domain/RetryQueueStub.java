package me.snowlight.domain;

import java.util.Collections;
import java.util.List;

class RetryQueueStub implements RetryQueue {

    private RetryDate retryDate;
    private int nth;

    @Override
    public void enQueue(RetryDate retryDate, int nth) {
        this.retryDate = retryDate;
        this.nth = nth;
    }

    @Override
    public List<RetryDate> deQueAll(int nth) {
        if (this.nth == nth) {
            return List.of(this.retryDate);
        }

        return Collections.emptyList();
    }
}
