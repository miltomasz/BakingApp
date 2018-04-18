package com.plumya.bakingapp.ui.list;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.plumya.bakingapp.data.BakingRepository;
import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.List;

/**
 * Created by miltomasz on 16/04/18.
 */

public class MainActivityViewModel extends ViewModel {
    private BakingRepository bakingRepository;
    private LiveData<List<RecipeEntry>> recipeEntries;

    public MainActivityViewModel(BakingRepository bakingRepository) {
        this.bakingRepository = bakingRepository;
        this.recipeEntries = bakingRepository.getRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return Transformations.map(recipeEntries, new Function<List<RecipeEntry>, List<Recipe>>() {
            @Override
            public List<Recipe> apply(List<RecipeEntry> input) {
                return RecipeUtils.toModel(input);
            }
        });
    }
}
