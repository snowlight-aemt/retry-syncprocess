package me.snowlight.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.snowlight.domain.queue.FileRetryQueue;
import me.snowlight.domain.queue.RetryData;
import me.snowlight.domain.queue.RetryQueue;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileRetryQueueTest {
    private final ObjectMapper mapper;

    public FileRetryQueueTest() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void post() throws Exception {
        Files.deleteIfExists(Paths.get("build/retry-first-files"));
        Files.deleteIfExists(Paths.get("build/retry-second-files"));
        Files.deleteIfExists(Paths.get("build/retry-third-files"));
    }

    @Test
    void enQueue__exists_first_files() throws Exception {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.enQueue(new RetryData(new TeamDao(1L, "TEST C", 122)), 1);

        boolean isRetryQueue = Files.exists(Paths.get("build/retry-first-files"));
        assertThat(isRetryQueue).isTrue();
    }

    @Test
    void enQueue__writing_file() throws Exception {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        RetryData retryData = new RetryData(new TeamDao(1L, "TEST C", 122));
        fileRetryQueue.enQueue(retryData, 1);

        List<String> lines = Files.readAllLines(Paths.get("build/retry-first-files"));
        RetryData sub = this.mapper.readValue(lines.get(0), RetryData.class);

        assertThat(retryData.getId()).isEqualTo(sub.getId());
        assertThat(retryData.getCreatedAt()).isEqualTo(sub.getCreatedAt());
    }

    @Test
    void enQueue__append_file() throws Exception {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        RetryData retryData1 = new RetryData(new TeamDao(1L, "TEST C", 122));
        fileRetryQueue.enQueue(retryData1, 1);
        RetryData retryData2 = new RetryData(new TeamDao(2L, "TEST C", 122));
        fileRetryQueue.enQueue(retryData2, 1);

        List<String> lines = Files.readAllLines(Paths.get("build/retry-first-files"));
        RetryData sub1 = this.mapper.readValue(lines.get(0), RetryData.class);
        assertThat(retryData1.getId()).isEqualTo(sub1.getId());
        assertThat(retryData1.getCreatedAt()).isEqualTo(sub1.getCreatedAt());

        RetryData sub2 = this.mapper.readValue(lines.get(1), RetryData.class);
        assertThat(retryData2.getId()).isEqualTo(sub2.getId());
        assertThat(retryData2.getCreatedAt()).isEqualTo(sub2.getCreatedAt());
    }

    @Test
    void enQueue__exists_second_files() {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.enQueue(new RetryData(new TeamDao(1L, "TEST C", 122)), 2);

        boolean isRetryQueue = Files.exists(Paths.get("build/retry-second-files"));
        assertThat(isRetryQueue).isTrue();
    }

    @Test
    void enQueue__exists_third_files() {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.enQueue(new RetryData(new TeamDao(1L, "TEST C", 122)), 3);

        boolean isRetryQueue = Files.exists(Paths.get("build/retry-third-files"));
        assertThat(isRetryQueue).isTrue();
    }

    @Test
    void enQueue__nth_then_great_third() {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        assertThatThrownBy(
        () -> {
            fileRetryQueue.enQueue(new RetryData(new TeamDao(1L, "TEST C", 122)), 4);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deQueAll__read_retry_first_file() throws Exception {
        RetryData retryData = new RetryData(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-first-files"), mapper.writeValueAsString(retryData));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        List<RetryData> retryDataList = fileRetryQueue.deQueAll(1);

        assertThat(retryDataList).hasSize(1);
        assertThat(retryDataList.get(0).getId()).isEqualTo(1L);
        assertThat(retryDataList.get(0).getName()).isEqualTo("Test A");
        assertThat(retryDataList.get(0).getMemberCount()).isEqualTo(123);
    }

    @Test
    void deQueAll__reading_after_empty() throws Exception {
        RetryData retryData = new RetryData(new TeamDao(1L, "Test A", 123));
        Path path = Paths.get("build/retry-first-files");
        Files.writeString(path, mapper.writeValueAsString(retryData));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.deQueAll(1);

        assertThat(Files.exists(path)).isFalse();
    }

    @Test
    void deQueAll__read_retry_second_file() throws Exception {
        RetryData retryData = new RetryData(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-second-files"), mapper.writeValueAsString(retryData));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        List<RetryData> retryDataList = fileRetryQueue.deQueAll(2);

        assertThat(retryDataList).hasSize(1);
        assertThat(retryDataList.get(0).getId()).isEqualTo(1L);
        assertThat(retryDataList.get(0).getName()).isEqualTo("Test A");
        assertThat(retryDataList.get(0).getMemberCount()).isEqualTo(123);
    }

    @Test
    void deQueAll__read_retry_third_file() throws Exception {
        RetryData retryData = new RetryData(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-third-files"), mapper.writeValueAsString(retryData));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        List<RetryData> retryDataList = fileRetryQueue.deQueAll(3);

        assertThat(retryDataList).hasSize(1);
        assertThat(retryDataList.get(0).getId()).isEqualTo(1L);
        assertThat(retryDataList.get(0).getName()).isEqualTo("Test A");
        assertThat(retryDataList.get(0).getMemberCount()).isEqualTo(123);
    }

    @Test
    void deQueAll__nth_then_great_third() throws Exception {
        RetryData retryData = new RetryData(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-third-files"), mapper.writeValueAsString(retryData));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        assertThatThrownBy(
                () -> {
                    fileRetryQueue.deQueAll(4);
                }).isInstanceOf(RuntimeException.class);
    }

}
