package com.plumya.bakingapp.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.plumya.bakingapp.data.BakingRepository;
import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.List;

/**
 * Created by miltomasz on 16/04/18.
 */

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();

    public MainActivityViewModel(BakingRepository bakingRepository) {
        bakingRepository.getRecipeEntries().observeForever(new Observer<List<RecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<RecipeEntry> recipeEntries) {
                if (recipesFound(recipeEntries)) {
                    recipes.postValue(RecipeUtils.toModel(recipeEntries));
                }
            }

            private boolean recipesFound(@Nullable List<RecipeEntry> recipeEntries) {
                return recipeEntries != null && recipeEntries.size() > 0;
            }
        });
    }

    /**
     * Exposes list of recipes for view as LiveData
     * @return
     */
    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}
