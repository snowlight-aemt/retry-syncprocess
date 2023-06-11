package me.snowlight.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class MemoryRetryQueue implements RetryQueue {
    HashMap<Integer, List<RetryDate>> memory = new HashMap<>();

    @Override
    public void enQueue(RetryDate retryDate, int nth) {
        List<RetryDate> retryDates = this.memory.get(nth);

        if (retryDates == null || retryDates.isEmpty()) {
            retryDates = new ArrayList<>();
        }

        retryDates.add(retryDate);
        this.memory.put(nth, retryDates);
    }

    @Override
    public List<RetryDate> deQueAll(int nth) {
        List<RetryDate> retryDates = this.memory.get(nth);
        if (this.memory.isEmpty() || retryDates == null)
            return Collections.emptyList();

        this.memory.remove(nth);
        return retryDates;
    }
}
