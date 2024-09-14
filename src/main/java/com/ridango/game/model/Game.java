package com.ridango.game.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Game {
    private int score = 0;
    private int highScore = 0;
    private int attemptsLeft = 5;
    private Set<String> usedCocktails = new HashSet<>();

    public void resetAttempts() {
        this.attemptsLeft = 5;
    }

    public void decreaseAttempt() {
        this.attemptsLeft--;
    }
}
