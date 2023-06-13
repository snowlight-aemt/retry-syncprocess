package me.snowlight.domain;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

public class SyncProcessIntegrationTest {
    @Test
    void name() {
        Sync mockSync = Mockito.mock(Sync.class);
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync);



//        mockSync.
    }
}
