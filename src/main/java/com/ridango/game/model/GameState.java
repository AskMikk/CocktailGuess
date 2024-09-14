package com.ridango.game.model;

import lombok.Data;
import java.util.List;

@Data
public class GameState {
    private int score;
    private int highScore;
    private int attemptsLeft;
    private List<Character> revealedName;
    private List<String> additionalInfo;
    private String instructions;
    private boolean gameOver;
    private String message;
    private String imageUrl;
}
