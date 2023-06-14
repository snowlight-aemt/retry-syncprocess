package me.snowlight.domain;

import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SyncProcessIntegrationTest {
    private Sync mockSync = Mockito.mock(Sync.class);
    private SyncProcess syncProcess;
    RetryQueue retryQueue = new MemoryRetryQueue();

    @BeforeEach
    public void setup() {
    }

    @Test
    void run__memoryQueue() {
        Sync sync = new Sync();
        this.syncProcess = new SyncProcess(sync, retryQueue);

        this.syncProcess.run(new TeamDao(101L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(102L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(103L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(104L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(105L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(106L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(107L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(108L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(109L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(110L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(111L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(112L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(113L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(114L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(115L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(116L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(117L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(118L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(119L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(120L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(121L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(122L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(123L, "TEAM_A", 500));

        new Schedule(new SyncRetry(sync, this.retryQueue)).run(1000, 2000, 3000);
    }

    @Test
    void run__fileQueue() {
        Sync sync = new Sync();
        FileRetryQueue mockMemoryRetryQueue = new FileRetryQueue();
        this.syncProcess = new SyncProcess(sync, mockMemoryRetryQueue);

        this.syncProcess.run(new TeamDao(101L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(102L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(103L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(104L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(105L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(106L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(107L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(108L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(109L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(110L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(111L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(112L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(113L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(114L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(115L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(116L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(117L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(118L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(119L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(120L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(121L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(122L, "TEAM_A", 500));
        this.syncProcess.run(new TeamDao(123L, "TEAM_A", 500));

        new Schedule(new SyncRetry(sync, mockMemoryRetryQueue)).run(1000, 2000, 3000);
    }

}
