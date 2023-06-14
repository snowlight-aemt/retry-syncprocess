package me.snowlight.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private class FileRetryQueue implements RetryQueue {
        public static final String RETRY_FIRST_PATH = "build/retry-first-files";
        public static final String RETRY_SECOND_PATH = "build/retry-second-files";
        public static final String RETRY_THIRD_PATH = "build/retry-third-files";
        private static final String EMPTY_PATH = "";

        private final ObjectMapper mapper;

        public FileRetryQueue() {
            mapper = new ObjectMapper();
        }

        @Override
        public void enQueue(RetryDate retryDate, int nth) {
            if (nth > 3) {
                throw new RuntimeException();
            }

            try {
                Files.writeString(getBackUpPath(nth), mapper.writeValueAsString(retryDate));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<RetryDate> deQueAll(int nth) {
            if (nth > 3) {
                throw new RuntimeException();
            }

            try {
                List<String> lines = Files.readAllLines(getBackUpPath(nth));
                List<RetryDate> retryDates = new ArrayList<>();

                for (String str : lines) {
                    retryDates.add(this.mapper.readValue(str, RetryDate.class));
                }

                return retryDates;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Path getBackUpPath(int nth) {
            if (nth == 1) {
                return Paths.get(RETRY_FIRST_PATH);
            } else if (nth == 2) {
                return Paths.get(RETRY_SECOND_PATH);
            } else if (nth == 3) {
                return Paths.get(RETRY_THIRD_PATH);
            }

            return Paths.get(EMPTY_PATH);
        }
    }
}