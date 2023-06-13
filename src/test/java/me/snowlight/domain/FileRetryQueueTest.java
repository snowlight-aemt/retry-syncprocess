package me.snowlight.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.snowlight.domain.team.TeamDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileRetryQueueTest {
    private final ObjectMapper mapper;

    public FileRetryQueueTest() {
        mapper = new ObjectMapper();
    }

    @Test
    void enQueue__creating_file() throws Exception {
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

    private class FileRetryQueue implements RetryQueue {
        public static final String RETRY_FILES = "build/retry-first-files";

        @Override
        public void enQueue(RetryDate retryDate, int nth) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                Files.writeString(Paths.get(RETRY_FILES), mapper.writeValueAsString(retryDate));
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
