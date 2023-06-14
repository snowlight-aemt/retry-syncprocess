package me.snowlight.domain;

import me.snowlight.domain.queue.RetryData;
import me.snowlight.domain.queue.RetryQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RetryQueueStub implements RetryQueue {

    Map<Integer, List<RetryData>> map = new HashMap<>();

    @Override
    public void enQueue(RetryData retryData, int nth) {
        List<RetryData> retryDataList = map.get(nth);
        if (retryDataList == null){
            retryDataList = new ArrayList<>();
            retryDataList.add(retryData);
            map.put(nth, retryDataList);
        } else
            retryDataList.add(retryData);
    }

    @Override
    public List<RetryData> deQueAll(int nth) {
        List<RetryData> retryData = map.get(nth);
        if (retryData == null || retryData.isEmpty())
            return Collections.emptyList();

        map.remove(nth);
        return retryData;
    }
}
