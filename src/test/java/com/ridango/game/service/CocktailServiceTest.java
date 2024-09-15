package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CocktailServiceTest {

    @InjectMocks
    private CocktailService cocktailService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(cocktailService, "randomCocktailUrl", "https://www.thecocktaildb.com/api/json/v1/1/random.php");
        ReflectionTestUtils.setField(cocktailService, "restTemplate", restTemplate);
    }

    @Test
    void getRandomCocktail_Success() throws Exception {
        String apiResponse = "{ \"drinks\": [{ \"idDrink\": \"11000\", \"strDrink\": \"Mojito\", \"strInstructions\": \"Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.\", \"strCategory\": \"Cocktail\", \"strGlass\": \"Highball glass\", \"strDrinkThumb\": \"https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg\", \"strIngredient1\": \"Light rum\", \"strIngredient2\": \"Lime\", \"strIngredient3\": \"Sugar\", \"strIngredient4\": \"Mint\", \"strIngredient5\": \"Soda water\" }]}";
        when(restTemplate.getForObject("https://www.thecocktaildb.com/api/json/v1/1/random.php", String.class)).thenReturn(apiResponse);

        Set<String> usedCocktails = new HashSet<>();
        Cocktail cocktail = cocktailService.getRandomCocktail(usedCocktails);

        assertNotNull(cocktail);
        assertEquals("11000", cocktail.getId());
        assertEquals("Mojito", cocktail.getName());
        assertEquals("Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.", cocktail.getInstructions());
        assertEquals("Cocktail", cocktail.getCategory());
        assertEquals("Highball glass", cocktail.getGlass());
        assertArrayEquals(new String[]{"Light rum", "Lime", "Sugar", "Mint", "Soda water"}, cocktail.getIngredients());
        assertEquals("https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg", cocktail.getImageUrl());
        assertTrue(usedCocktails.contains("11000"));
    }

    @Test
    void getRandomCocktail_ApiResponseNull() {
        when(restTemplate.getForObject("https://www.thecocktaildb.com/api/json/v1/1/random.php", String.class)).thenReturn(null);

        Set<String> usedCocktails = new HashSet<>();
        Cocktail cocktail = cocktailService.getRandomCocktail(usedCocktails);

        assertNull(cocktail);
    }

    @Test
    void getRandomCocktail_InvalidJson() {
        String invalidJson = "Invalid JSON";
        when(restTemplate.getForObject("https://www.thecocktaildb.com/api/json/v1/1/random.php", String.class)).thenReturn(invalidJson);

        Set<String> usedCocktails = new HashSet<>();
        Cocktail cocktail = cocktailService.getRandomCocktail(usedCocktails);

        assertNull(cocktail);
    }

    @Test
    void getRandomCocktail_NoDrinks() {
        String apiResponse = "{ \"drinks\": [] }";
        when(restTemplate.getForObject("https://www.thecocktaildb.com/api/json/v1/1/random.php", String.class)).thenReturn(apiResponse);

        Set<String> usedCocktails = new HashSet<>();
        Cocktail cocktail = cocktailService.getRandomCocktail(usedCocktails);

        assertNull(cocktail);
    }

    @Test
    void getRandomCocktail_UsedCocktailRetriesAndThrowsException() {
        String apiResponse = "{ \"drinks\": [{ \"idDrink\": \"11000\", \"strDrink\": \"Mojito\", \"strInstructions\": \"Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish and serve with straw.\", \"strCategory\": \"Cocktail\", \"strGlass\": \"Highball glass\", \"strDrinkThumb\": \"https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg\", \"strIngredient1\": \"Light rum\", \"strIngredient2\": \"Lime\", \"strIngredient3\": \"Sugar\", \"strIngredient4\": \"Mint\", \"strIngredient5\": \"Soda water\" }]}";
        when(restTemplate.getForObject("https://www.thecocktaildb.com/api/json/v1/1/random.php", String.class)).thenReturn(apiResponse);

        Set<String> usedCocktails = new HashSet<>();
        usedCocktails.add("11000");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cocktailService.getRandomCocktail(usedCocktails);
        });

        assertEquals("No new cocktails available after maximum retries", exception.getMessage());

        verify(restTemplate, times(635)).getForObject("https://www.thecocktaildb.com/api/json/v1/1/random.php", String.class);
    }
}
