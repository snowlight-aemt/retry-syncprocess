package me.snowlight.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

class SyncProcessTest {
    Sync mockSync = Mockito.mock(Sync.class);

    @Test
    void sync() {
        BDDMockito
                .willThrow(RuntimeException.class)
                .given(mockSync).sync();

        SyncProcess syncProcess = new SyncProcess(mockSync);
        SyncResult syncResult = syncProcess.run();
        
        Assertions.assertThat(syncResult).isEqualTo(SyncResult.FAILED);
    }

    private class SyncProcess {

        private Sync sync;

        public SyncProcess(Sync sync) {
            this.sync = sync;
        }

        public SyncResult run() {
            try {
                this.sync.sync();
            } catch (RuntimeException e) {
                return SyncResult.FAILED;
            }

            return SyncResult.SUCCESS;
        }
    }

    private enum SyncResult {
        SUCCESS, FAILED
    }

    private class Sync {
        public void sync() {
        }
    }
}