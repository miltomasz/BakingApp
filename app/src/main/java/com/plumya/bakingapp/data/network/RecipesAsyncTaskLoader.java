package com.plumya.bakingapp.data.network;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.plumya.bakingapp.data.model.Recipe;

import java.util.List;

/**
 * Created by miltomasz on 13/04/18.
 */

public class RecipesAsyncTaskLoader extends AsyncTaskLoader<List<Recipe>> {

    public RecipesAsyncTaskLoader(Context context) {
        super(context);
    }

    private static boolean initilized;
    private List<Recipe> recipes;

    public RecipesAsyncTaskLoader(Context context, String s) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (recipes != null) {
            deliverResult(recipes);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Recipe> loadInBackground() {
        if (initilized) {
//            RecipeDao recipeDao = Injector.pro
        }

        return null;
    }

    @Override
    public void deliverResult(List<Recipe> recipes) {
        this.recipes = recipes;
        super.deliverResult(recipes);
    }
}
