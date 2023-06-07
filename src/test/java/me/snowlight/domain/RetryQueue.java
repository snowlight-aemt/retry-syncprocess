package me.snowlight.domain;

import java.util.List;

public interface RetryQueue {
    void enQueue(RetryDate retryDate, int nth);
    List<RetryDate> deQueAll(int nth);
}
