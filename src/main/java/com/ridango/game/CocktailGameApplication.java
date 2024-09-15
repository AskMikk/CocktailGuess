package com.ridango.game;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class CocktailGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(CocktailGameApplication.class, args);
	}
}
