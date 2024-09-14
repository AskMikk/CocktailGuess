package com.ridango.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cocktail {
    private String id;
    private String name;
    private String instructions;
    private String category;
    private String glass;
    private String[] ingredients;
    private String imageUrl;
}
