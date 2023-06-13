package me.snowlight.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileRetryQueueTest {
    private final ObjectMapper mapper;

    public FileRetryQueueTest() {
        mapper = new ObjectMapper();
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

    private class FileRetryQueue implements RetryQueue {
        public static final String RETRY_FIRST_FILES = "build/retry-first-files";
        public static final String RETRY_SECOND_FILES = "build/retry-second-files";
        public static final String RETRY_THIRD_FILES = "build/retry-third-files";

        @Override
        public void enQueue(RetryDate retryDate, int nth) {
            if (nth > 3) {
                throw new RuntimeException();
            }

            ObjectMapper mapper = new ObjectMapper();

            String retryFiles = "";
            if (nth == 1)
                retryFiles = RETRY_FIRST_FILES;
            else if (nth == 2)
                retryFiles = RETRY_SECOND_FILES;
            else if (nth == 3)
                retryFiles = RETRY_THIRD_FILES;

            try {
                Files.writeString(Paths.get(retryFiles), mapper.writeValueAsString(retryDate));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<RetryDate> deQueAll(int nth) {
            return null;
        }
    }

    //    @Test
//    void enQueueAndDeQueAll() {
//        RetryQueue fileRetryQueue = new FileRetryQueue();
//
//        RetryDate retryDate1 = new RetryDate(new TeamDao(1L, "Test C", 13));
//        RetryDate retryDate2 = new RetryDate(new TeamDao(2L, "Test C", 13));
//        fileRetryQueue.enQueue(retryDate1, 1);
//        fileRetryQueue.enQueue(retryDate2, 1);
//
//        List<RetryDate> retryDates = fileRetryQueue.deQueAll(1);
//
//        assertThat(retryDates).hasSize(2);
//        assertThat(retryDates).contains(retryDate1, retryDate2);
//    }
//
//    private class FileRetryQueue implements RetryQueue {
//        ObjectMapper objectMapper;
//
//        public FileRetryQueue() {
//            this.objectMapper = new ObjectMapper();
//        }
//
//        @Override
//        public void enQueue(RetryDate retryDate, int nth) {
//            try {
//                String str = objectMapper.writeValueAsString(retryDate);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//
//            Files.write(Paths.get("build/firstRetryData"), )
//        }
//
//        @Override
//        public List<RetryDate> deQueAll(int nth) {
//            return null;
//        }
//    }
}
