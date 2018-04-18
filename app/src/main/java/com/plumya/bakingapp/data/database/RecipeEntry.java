package com.plumya.bakingapp.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by miltomasz on 13/04/18.
 */

@Entity(tableName = "recipes")
public class RecipeEntry {
    @PrimaryKey
    private long id;
    private String name;
    private String ingredients;
    private String steps;
    private int servings;
    private String image;

    // Constructor used by Room to create RecipeEntries
    public RecipeEntry(long id, String name, String ingredients, String steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }
}
