package me.snowlight.domain.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MemoryRetryQueue implements RetryQueue {
    HashMap<Integer, List<RetryData>> memory = new HashMap<>();

    @Override
    public void enQueue(RetryData retryData, int nth) {
        List<RetryData> retryDataList = this.memory.get(nth);

        if (retryDataList == null || retryDataList.isEmpty()) {
            retryDataList = new ArrayList<>();
        }

        retryDataList.add(retryData);
        this.memory.put(nth, retryDataList);
    }

    @Override
    public List<RetryData> deQueAll(int nth) {
        List<RetryData> retryData = this.memory.get(nth);
        if (this.memory.isEmpty() || retryData == null)
            return Collections.emptyList();

        this.memory.remove(nth);
        return retryData;
    }
}
