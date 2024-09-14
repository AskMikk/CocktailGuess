package com.ridango.game.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HighScoreService {

    @Value("${highscore.file.path}")
    private String highScoreFilePath;

    private static final Logger logger = LoggerFactory.getLogger(HighScoreService.class);

    public int getHighScore() {
        int highScore = 0;
        File file = new File(highScoreFilePath);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                highScore = Integer.parseInt(line);
            } catch (IOException e) {
                logger.error("Error reading high score file", e);
            } catch (NumberFormatException e) {
                logger.error("Invalid high score format in file", e);
            }
        } else {
            logger.info("High score file does not exist. Starting with high score of 0.");
        }
        return highScore;
    }

    public void saveHighScore(int highScore) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(highScoreFilePath))) {
            bw.write(String.valueOf(highScore));
        } catch (IOException e) {
            logger.error("Error writing high score to file", e);
        }
    }
}
