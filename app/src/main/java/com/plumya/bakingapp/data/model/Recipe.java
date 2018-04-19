package com.plumya.bakingapp.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by miltomasz on 13/04/18.
 */

public class Recipe implements Serializable {
    public long id;
    public String name;
    public List<Ingredient> ingredients;
    public List<Step> steps;
    public int servings;
    public String image;

    public Recipe(long id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }
}
