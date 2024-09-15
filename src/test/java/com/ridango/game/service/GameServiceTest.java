package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private CocktailService cocktailService;

    @Mock
    private HighScoreService highScoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Cocktail mockCocktail = new Cocktail(
                "11000",
                "Mojito",
                "Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.",
                "Cocktail",
                "Highball glass",
                new String[]{"Light rum", "Lime", "Sugar", "Mint", "Soda water"},
                "https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg"
        );
        when(highScoreService.getHighScore()).thenReturn(50);
        when(cocktailService.getRandomCocktail(anySet())).thenReturn(mockCocktail);
    }

    @Test
    void restartGame_FirstTime() {
        GameState gameState = gameService.restartGame();
        assertEquals(0, gameState.getScore());
        assertEquals(50, gameState.getHighScore());
        assertEquals(5, gameState.getAttemptsLeft());
        assertEquals("Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.", gameState.getInstructions());
        assertEquals(List.of('_', '_', '_', '_', '_', '_'), gameState.getRevealedName());
        assertTrue(gameState.getAdditionalInfo().isEmpty());
        assertFalse(gameState.isGameOver());
        assertEquals("Game restarted", gameState.getMessage());
        assertEquals("https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg", gameState.getImageUrl());
        verify(cocktailService, times(1)).getRandomCocktail(anySet());
    }

    @Test
    void makeGuess_CorrectGuess() {
        gameService.restartGame();
        GameState gameState = gameService.makeGuess("Mojito");
        assertEquals(5, gameState.getScore());
        assertEquals(50, gameState.getHighScore());
        assertEquals(5, gameState.getAttemptsLeft());
        assertEquals("Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.", gameState.getInstructions());
        assertEquals(List.of('_', '_', '_', '_', '_', '_'), gameState.getRevealedName());
        assertTrue(gameState.getAdditionalInfo().isEmpty());
        assertFalse(gameState.isGameOver());
        assertEquals("Correct! Starting next round", gameState.getMessage());
        assertEquals("https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg", gameState.getImageUrl());
        verify(cocktailService, times(2)).getRandomCocktail(anySet());
    }

    @Test
    void makeGuess_IncorrectGuess_DecreaseAttempt() {
        gameService.restartGame();
        GameState gameState = gameService.makeGuess("Martini");
        assertEquals(4, gameState.getAttemptsLeft());
        assertEquals(0, gameState.getScore());
        assertEquals(50, gameState.getHighScore());
        assertEquals("Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.", gameState.getInstructions());
        assertEquals(3, gameState.getRevealedName().stream().filter(c -> c != '_').count());
        assertEquals(1, gameState.getAdditionalInfo().size());
        assertFalse(gameState.isGameOver());
        assertNull(gameState.getMessage());
        verify(cocktailService, times(1)).getRandomCocktail(anySet());
    }

    @Test
    void makeGuess_InvalidGuess_Empty() {
        gameService.restartGame();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.makeGuess("");
        });
        assertEquals("Guess cannot be empty", exception.getMessage());
    }

    @Test
    void makeGuess_InvalidGuess_Null() {
        gameService.restartGame();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.makeGuess(null);
        });
        assertEquals("Guess cannot be empty", exception.getMessage());
    }
}
