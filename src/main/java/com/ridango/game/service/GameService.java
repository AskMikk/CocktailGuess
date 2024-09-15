package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.Game;
import com.ridango.game.model.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;

@Service
@SessionScope
@RequiredArgsConstructor
public class GameService {

    private Game game;
    private Cocktail currentCocktail;
    private char[] revealedName;
    private int infoRevealedCount;
    private List<String> additionalInfoList;

    private final CocktailService cocktailService;
    private final HighScoreService highScoreService;

    public GameState restartGame() {
        if (game == null) {
            game = new Game();
        }
        game.setHighScore(highScoreService.getHighScore());
        game.setScore(0);
        game.resetAttempts();
        game.getUsedCocktails().clear();
        initializeCurrentCocktail();
        return getGameState("Game restarted");
    }

    private void initializeCurrentCocktail() {
        currentCocktail = cocktailService.getRandomCocktail(game.getUsedCocktails());
        if (currentCocktail == null) {
            throw new RuntimeException("Failed to fetch cocktail");
        }

        revealedName = new char[currentCocktail.getName().length()];
        for (int i = 0; i < currentCocktail.getName().length(); i++) {
            char c = currentCocktail.getName().charAt(i);
            revealedName[i] = (c == ' ') ? ' ' : '_';
        }
        infoRevealedCount = 0;
        prepareAdditionalInfo();
    }

    private void prepareAdditionalInfo() {
        additionalInfoList = new ArrayList<>();
        additionalInfoList.add("Category: " + currentCocktail.getCategory());
        additionalInfoList.add("Glass Type: " + currentCocktail.getGlass());
        additionalInfoList.add("Ingredients: " + String.join(", ", currentCocktail.getIngredients()));
        Collections.shuffle(additionalInfoList);
    }

    public GameState makeGuess(String guess) {
        if (guess == null || guess.trim().isEmpty()) {
            throw new IllegalArgumentException("Guess cannot be empty");
        }

        if (guess.equalsIgnoreCase(currentCocktail.getName())) {
            game.setScore(game.getScore() + game.getAttemptsLeft());
            return startNextRound();
        } else {
            game.decreaseAttempt();
            revealLetters();
            revealAdditionalInfo();
            if (game.getAttemptsLeft() <= 0) {
                revealFullName();
                String message = "Game Over! The correct answer was: " + currentCocktail.getName();
                updateHighScore();
                return getGameState(message, true);
            }
            return getGameState(null, false);
        }
    }

    private void revealFullName() {
        for (int i = 0; i < revealedName.length; i++) {
            if (revealedName[i] == '_') {
                revealedName[i] = currentCocktail.getName().charAt(i);
            }
        }
    }

    private void revealLetters() {
        int nameLength = currentCocktail.getName().replaceAll(" ", "").length();
        int lettersToReveal = Math.max(nameLength / 4, 1);
        Random random = new Random();
        List<Integer> unrevealedIndices = new ArrayList<>();
        for (int i = 0; i < revealedName.length; i++) {
            if (revealedName[i] == '_') {
                unrevealedIndices.add(i);
            }
        }

        if (unrevealedIndices.size() <= 1) {
            return;
        }

        lettersToReveal = Math.min(lettersToReveal, unrevealedIndices.size() - 1);

        for (int i = 0; i < lettersToReveal && !unrevealedIndices.isEmpty(); i++) {
            int index = unrevealedIndices.remove(random.nextInt(unrevealedIndices.size()));
            revealedName[index] = currentCocktail.getName().charAt(index);
        }
    }

    private void revealAdditionalInfo() {
        if (infoRevealedCount < additionalInfoList.size()) {
            infoRevealedCount++;
        }
    }

    private GameState getGameState(String message) {
        return getGameState(message, false);
    }

    private GameState getGameState(String message, boolean gameOver) {
        GameState state = new GameState();
        state.setScore(game.getScore());
        state.setHighScore(game.getHighScore());
        state.setAttemptsLeft(game.getAttemptsLeft());
        state.setInstructions(currentCocktail.getInstructions());
        state.setRevealedName(convertCharArrayToList(revealedName));
        state.setAdditionalInfo(additionalInfoList.subList(0, infoRevealedCount));
        state.setGameOver(gameOver);
        state.setMessage(message);
        state.setImageUrl(currentCocktail.getImageUrl());
        return state;
    }

    private GameState startNextRound() {
        game.resetAttempts();
        initializeCurrentCocktail();
        return getGameState("Correct! Starting next round");
    }

    private void updateHighScore() {
        if (game.getScore() > game.getHighScore()) {
            game.setHighScore(game.getScore());
            highScoreService.saveHighScore(game.getHighScore());
        }
    }

    private List<Character> convertCharArrayToList(char[] array) {
        List<Character> list = new ArrayList<>(array.length);
        for (char c : array) {
            list.add(c);
        }
        return list;
    }
}
