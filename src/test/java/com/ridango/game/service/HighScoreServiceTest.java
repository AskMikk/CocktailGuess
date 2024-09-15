package com.ridango.game.service;

import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighScoreServiceTest {

    private HighScoreService highScoreService;
    private Path highScoreFilePath;

    @BeforeEach
    void setUp() throws IOException {
        highScoreService = new HighScoreService();

        highScoreFilePath = Files.createTempFile("highscore", ".txt");

        ReflectionTestUtils.setField(highScoreService, "highScoreFilePath", highScoreFilePath.toString());
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(highScoreFilePath);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
    }

    @Test
    void testInvalidHighScoreFormat() throws IOException {
        Files.writeString(highScoreFilePath, "invalid");

        int highScore = highScoreService.getHighScore();

        assertEquals(0, highScore);
    }
}
