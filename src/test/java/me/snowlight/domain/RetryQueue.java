package me.snowlight.domain;

import java.util.List;

public interface RetryQueue {
    void enQueue(SyncProcessTest.RetryDate retryDate, int nth);
    List<SyncProcessTest.RetryDate> deQueAll(int nth);
}
