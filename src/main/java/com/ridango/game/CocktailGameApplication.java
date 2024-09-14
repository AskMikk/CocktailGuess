package com.ridango.game;

import com.ridango.game.model.GameState;
import com.ridango.game.service.GameService;
import com.ridango.game.utils.InputReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class CocktailGameApplication implements CommandLineRunner {

	private final GameService gameService;
	private final ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(CocktailGameApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Welcome to the Cocktail Guessing Game!");

		while (true) {
			GameState gameState = gameService.restartGame();
			playGame(gameState);

			String playAgain = InputReader.readLine("Do you want to play again? (yes/no): ");
			if (!playAgain.equalsIgnoreCase("yes")) {
				break;
			}
		}

		System.out.println("Thanks for playing! Goodbye!");
		SpringApplication.exit(context);
	}

	private void playGame(GameState gameState) {
		while (!gameState.isGameOver()) {
			printGameState(gameState);

			String guess = InputReader.readLine("Your guess: ");
			gameState = gameService.makeGuess(guess);

			if (gameState.isGameOver()) {
				printGameState(gameState);
				System.out.println("Game Over! " + gameState.getMessage());
				System.out.println("High Score: " + gameState.getHighScore());
			}
		}
	}

	private static void printGameState(GameState gameState) {
		System.out.println("\nCurrent Score: " + gameState.getScore());
		System.out.println("Attempts Left: " + gameState.getAttemptsLeft());
		System.out.println("Cocktail Name: " + convertCharListToString(gameState.getRevealedName()));
		System.out.println("Additional Info: " + gameState.getAdditionalInfo());
		System.out.println("Instructions: " + gameState.getInstructions());
	}

	private static String convertCharListToString(List<Character> charList) {
		StringBuilder sb = new StringBuilder(charList.size());
		for (Character c : charList) {
			sb.append(c);
		}
		return sb.toString();
	}
}
