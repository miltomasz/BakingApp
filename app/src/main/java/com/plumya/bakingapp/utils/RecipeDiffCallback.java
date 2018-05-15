package com.plumya.bakingapp.utils;

import android.support.v7.util.DiffUtil;

import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Recipe;

import java.util.List;

/**
 * Created by miltomasz on 11/05/18.
 */

public class RecipeDiffCallback extends DiffUtil.Callback {

    private final List<RecipeEntry> recipeEntries;
    private final List<Recipe> recipes;

    public RecipeDiffCallback(List<RecipeEntry> recipeEntries, List<Recipe> recipes) {
        this.recipeEntries = recipeEntries;
        this.recipes = recipes;
    }

    @Override
    public int getOldListSize() {
        return recipeEntries.size();
    }

    @Override
    public int getNewListSize() {
        return recipes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return recipeEntries.get(oldItemPosition).getId() == recipes.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        RecipeEntry recipeEntry = recipeEntries.get(oldItemPosition);
        Recipe recipe = recipes.get(newItemPosition);
        return recipeEntry.getName().equals(recipe.name);
    }
}
