package com.ridango.game.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridango.game.model.Cocktail;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CocktailService {

    @Value("${cocktail.api.url}")
    private String randomCocktailUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(CocktailService.class);

    public Cocktail getRandomCocktail(Set<String> usedCocktails) {
        int maxRetries = 635;
        int retries = 0;
        while (retries < maxRetries) {
            String response = restTemplate.getForObject(randomCocktailUrl, String.class);
            if (response == null) {
                logger.error("API response is null");
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(response);
            } catch (Exception e) {
                logger.error("Failed to parse API response", e);
                return null;
            }

            JsonNode drinksNode = jsonNode.get("drinks");
            if (drinksNode == null || !drinksNode.elements().hasNext()) {
                logger.error("No drinks found in API response");
                return null;
            }

            JsonNode drink = drinksNode.get(0);
            String id = drink.get("idDrink").asText();

            if (!usedCocktails.contains(id)) {
                usedCocktails.add(id);
                return parseCocktail(drink);
            }
            retries++;
        }
        throw new RuntimeException("No new cocktails available after maximum retries");
    }

    private Cocktail parseCocktail(JsonNode drink) {
        String id = drink.get("idDrink").asText();
        String name = drink.get("strDrink").asText();
        String instructions = drink.get("strInstructions").asText();
        String category = drink.get("strCategory").asText();
        String glass = drink.get("strGlass").asText();
        String imageUrl = drink.get("strDrinkThumb").asText();
        String[] ingredients = getIngredients(drink);

        return new Cocktail(id, name, instructions, category, glass, ingredients, imageUrl);
    }

    private String[] getIngredients(JsonNode drink) {
        List<String> ingredients = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String key = "strIngredient" + i;
            if (drink.has(key)) {
                JsonNode ingredientNode = drink.get(key);
                if (!ingredientNode.isNull() && !ingredientNode.asText().isEmpty()) {
                    ingredients.add(ingredientNode.asText());
                }
            }
        }
        return ingredients.toArray(new String[0]);
    }
}
