package seng202.team4.unittests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import seng202.team4.repository.DatabaseManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TestSuite {

    private static final String TEST_DB = "jdbc:sqlite:build/test-database.db";

    @BeforeEach
    void resetTestDatabase() throws IOException {
        Files.deleteIfExists(Path.of("build/test-database.db"));
    }

    public String getTestDb() {
        return TEST_DB;
    }
}
