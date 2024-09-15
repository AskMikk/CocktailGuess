package com.ridango.game.controller;

import com.ridango.game.model.GameState;
import com.ridango.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/guess")
    public GameState makeGuess(@RequestBody Map<String, String> payload) {
        String guess = payload.get("guess");
        return gameService.makeGuess(guess);
    }

    @GetMapping("/restart")
    public ResponseEntity<GameState> restartGame() {
        GameState gameState = gameService.restartGame();
        return ResponseEntity.ok(gameState);
    }
}
