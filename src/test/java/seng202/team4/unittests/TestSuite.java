package seng202.team4.unittests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class TestSuite {

    private static final String TEST_DB = "jdbc:sqlite:build/test-database.db";

    @BeforeEach
    void resetTestDatabase() throws IOException {
        Files.deleteIfExists(Path.of("build/test-database.db"));
    }

    public String getTestDb() {
        return TEST_DB;
    }

    @AfterEach
    void deleteDatabase() throws IOException {
         Files.deleteIfExists(Path.of("build/test-database.db"));
    }
}
