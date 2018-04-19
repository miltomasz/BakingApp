package com.plumya.bakingapp.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Ingredient;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.model.Step;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miltomasz on 13/04/18.
 */

public class RecipeUtils {

    private static Gson gson = new Gson();

    private RecipeUtils() {}

    public static Recipe toModel(RecipeEntry recipeEntry) {
        List<Ingredient> ingredients = gson.fromJson(recipeEntry.getIngredients(),
                new TypeToken<List<Ingredient>>(){}.getType());
        List<Step> steps = gson.fromJson(recipeEntry.getSteps(),
                new TypeToken<List<Step>>(){}.getType());
        return new Recipe(recipeEntry.getId(),
                recipeEntry.getName(),
                ingredients,
                steps,
                recipeEntry.getServings(),
                recipeEntry.getImage()
        );
    }

    public static List<Recipe> toModel(List<RecipeEntry> recipeEntries) {
        List<Recipe> recipes = new ArrayList<>();
        for (RecipeEntry re : recipeEntries) {
            recipes.add(toModel(re));
        }
        return recipes;
    }

    public static RecipeEntry toEntry(Recipe recipe) {
        Type listOfIngredientType = new TypeToken<List<Ingredient>>() {}.getType();
        String ingredients = gson.toJson(recipe.ingredients, listOfIngredientType);
        Type listOfStepType = new TypeToken<List<Step>>() {}.getType();
        String steps = gson.toJson(recipe.steps, listOfStepType);
        return new RecipeEntry(recipe.id, recipe.name, ingredients, steps, recipe.servings, recipe.image);
    }

    public static RecipeEntry[] toEntries(List<Recipe> recipes) {
        RecipeEntry[] recipeEntries = new RecipeEntry[recipes.size()];
        for (int i = 0; i < recipeEntries.length; i++) {
            recipeEntries[i] = RecipeUtils.toEntry(recipes.get(i));
        }
        return recipeEntries;
    }

    public static String displayIngredients(Context context, Recipe recipe) {
        if (recipe == null || recipe.ingredients == null) {
            return context.getString(R.string.no_ingredients_msg);
        }
        List<String> ingredients = new ArrayList<>();
        for (Ingredient ingredient : recipe.ingredients) {
            ingredients.add(ingredient.ingredient);
        }
        return TextUtils.join(", ", ingredients);
    }
}
