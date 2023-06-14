package me.snowlight.domain.queue;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileRetryQueue implements RetryQueue {
    public static final String RETRY_FIRST_PATH = "build/retry-first-files";
    public static final String RETRY_SECOND_PATH = "build/retry-second-files";
    public static final String RETRY_THIRD_PATH = "build/retry-third-files";
    private static final String EMPTY_PATH = "";

    private final ObjectMapper mapper;

    public FileRetryQueue() {
        mapper = new ObjectMapper();
    }

    @Override
    public void enQueue(RetryData retryData, int nth) {
        if (nth > 3) {
            throw new RuntimeException();
        }

        Path backUpPath = getBackUpPath(nth);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(backUpPath,
                                                            StandardOpenOption.CREATE,
                                                            StandardOpenOption.APPEND)) {

            bufferedWriter.write(mapper.writeValueAsString(retryData));
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RetryData> deQueAll(int nth) {
        if (nth > 3) {
            throw new RuntimeException();
        }

        try {
            Path backUpPath = getBackUpPath(nth);
            List<String> lines = Files.readAllLines(backUpPath);
            List<RetryData> retryData = new ArrayList<>();

            for (String str : lines) {
                retryData.add(this.mapper.readValue(str, RetryData.class));
            }

            Files.delete(backUpPath);
            return retryData;
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
