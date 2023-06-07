package me.snowlight.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RetryQueueStub implements RetryQueue {

    Map<Integer, List<RetryDate>> map = new HashMap<>();

    @Override
    public void enQueue(RetryDate retryDate, int nth) {
        List<RetryDate> retryDates = map.get(nth);
        if (retryDates == null){
            retryDates = new ArrayList<>();
            retryDates.add(retryDate);
            map.put(nth, retryDates);
        } else
            retryDates.add(retryDate);
    }

    @Override
    public List<RetryDate> deQueAll(int nth) {
        List<RetryDate> retryDates = map.get(nth);
        if (retryDates == null && retryDates.isEmpty())
            return Collections.emptyList();

        return retryDates;
    }
}
