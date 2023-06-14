package me.snowlight.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        fileRetryQueue.enQueue(new RetryDate(new TeamDao(1L, "TEST C", 122)), 1);

        boolean isRetryQueue = Files.exists(Paths.get("build/retry-first-files"));
        assertThat(isRetryQueue).isTrue();
    }

    @Test
    void enQueue__writing_file() throws Exception {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        RetryDate retryDate = new RetryDate(new TeamDao(1L, "TEST C", 122));
        fileRetryQueue.enQueue(retryDate, 1);

        List<String> lines = Files.readAllLines(Paths.get("build/retry-first-files"));
        RetryDate sub = this.mapper.readValue(lines.get(0), RetryDate.class);

        assertThat(retryDate.getId()).isEqualTo(sub.getId());
        assertThat(retryDate.getCreatedAt()).isEqualTo(sub.getCreatedAt());
    }

    @Test
    void enQueue__append_file() throws Exception {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        RetryDate retryDate1 = new RetryDate(new TeamDao(1L, "TEST C", 122));
        fileRetryQueue.enQueue(retryDate1, 1);
        RetryDate retryDate2 = new RetryDate(new TeamDao(2L, "TEST C", 122));
        fileRetryQueue.enQueue(retryDate2, 1);

        List<String> lines = Files.readAllLines(Paths.get("build/retry-first-files"));
        RetryDate sub1 = this.mapper.readValue(lines.get(0), RetryDate.class);
        assertThat(retryDate1.getId()).isEqualTo(sub1.getId());
        assertThat(retryDate1.getCreatedAt()).isEqualTo(sub1.getCreatedAt());

        RetryDate sub2 = this.mapper.readValue(lines.get(1), RetryDate.class);
        assertThat(retryDate2.getId()).isEqualTo(sub2.getId());
        assertThat(retryDate2.getCreatedAt()).isEqualTo(sub2.getCreatedAt());
    }

    @Test
    void enQueue__exists_second_files() {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.enQueue(new RetryDate(new TeamDao(1L, "TEST C", 122)), 2);

        boolean isRetryQueue = Files.exists(Paths.get("build/retry-second-files"));
        assertThat(isRetryQueue).isTrue();
    }

    @Test
    void enQueue__exists_third_files() {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.enQueue(new RetryDate(new TeamDao(1L, "TEST C", 122)), 3);

        boolean isRetryQueue = Files.exists(Paths.get("build/retry-third-files"));
        assertThat(isRetryQueue).isTrue();
    }

    @Test
    void enQueue__nth_then_great_third() {
        RetryQueue fileRetryQueue = new FileRetryQueue();
        assertThatThrownBy(
        () -> {
            fileRetryQueue.enQueue(new RetryDate(new TeamDao(1L, "TEST C", 122)), 4);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void deQueAll__read_retry_first_file() throws Exception {
        RetryDate retryDate = new RetryDate(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-first-files"), mapper.writeValueAsString(retryDate));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        List<RetryDate> retryDates = fileRetryQueue.deQueAll(1);

        assertThat(retryDates).hasSize(1);
        assertThat(retryDates.get(0).getId()).isEqualTo(1L);
        assertThat(retryDates.get(0).getName()).isEqualTo("Test A");
        assertThat(retryDates.get(0).getMemberCount()).isEqualTo(123);
    }

    @Test
    void deQueAll__reading_after_empty() throws Exception {
        RetryDate retryDate = new RetryDate(new TeamDao(1L, "Test A", 123));
        Path path = Paths.get("build/retry-first-files");
        Files.writeString(path, mapper.writeValueAsString(retryDate));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        fileRetryQueue.deQueAll(1);

        assertThat(Files.exists(path)).isFalse();
    }

    @Test
    void deQueAll__read_retry_second_file() throws Exception {
        RetryDate retryDate = new RetryDate(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-second-files"), mapper.writeValueAsString(retryDate));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        List<RetryDate> retryDates = fileRetryQueue.deQueAll(2);

        assertThat(retryDates).hasSize(1);
        assertThat(retryDates.get(0).getId()).isEqualTo(1L);
        assertThat(retryDates.get(0).getName()).isEqualTo("Test A");
        assertThat(retryDates.get(0).getMemberCount()).isEqualTo(123);
    }

    @Test
    void deQueAll__read_retry_third_file() throws Exception {
        RetryDate retryDate = new RetryDate(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-third-files"), mapper.writeValueAsString(retryDate));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        List<RetryDate> retryDates = fileRetryQueue.deQueAll(3);

        assertThat(retryDates).hasSize(1);
        assertThat(retryDates.get(0).getId()).isEqualTo(1L);
        assertThat(retryDates.get(0).getName()).isEqualTo("Test A");
        assertThat(retryDates.get(0).getMemberCount()).isEqualTo(123);
    }

    @Test
    void deQueAll__nth_then_great_third() throws Exception {
        RetryDate retryDate = new RetryDate(new TeamDao(1L, "Test A", 123));
        Files.writeString(Paths.get("build/retry-third-files"), mapper.writeValueAsString(retryDate));

        FileRetryQueue fileRetryQueue = new FileRetryQueue();
        assertThatThrownBy(
                () -> {
                    fileRetryQueue.deQueAll(4);
                }).isInstanceOf(RuntimeException.class);
    }

}
