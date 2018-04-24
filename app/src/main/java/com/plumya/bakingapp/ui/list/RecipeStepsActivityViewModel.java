package com.plumya.bakingapp.ui.list;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.plumya.bakingapp.data.BakingRepository;
import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.utils.RecipeUtils;

/**
 * Created by miltomasz on 18/04/18.
 */

public class RecipeStepsActivityViewModel extends ViewModel {

    private LiveData<RecipeEntry> recipeEntry;
    private final BakingRepository bakingRepository;

    public RecipeStepsActivityViewModel(BakingRepository repository) {
        this.bakingRepository = repository;
//        this.recipeEntry = bakingRepository.getRecipe(recipeId);
    }

    public void selectRecipeId(long recipeId) {
        this.recipeEntry = bakingRepository.getRecipe(recipeId);
    }

    public LiveData<Recipe> getRecipe() {
        return Transformations.map(recipeEntry, new Function<RecipeEntry, Recipe>() {
            @Override
            public Recipe apply(RecipeEntry recipeEntry) {
                return RecipeUtils.toModel(recipeEntry);
            }
        });
    }
}
