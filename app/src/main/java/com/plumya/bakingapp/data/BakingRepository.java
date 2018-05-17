package com.plumya.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.util.Log;

import com.plumya.bakingapp.data.database.IngredientDao;
import com.plumya.bakingapp.data.database.RecipeDao;
import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.network.RecipeNetworkDataSource;
import com.plumya.bakingapp.utils.AppExecutors;
import com.plumya.bakingapp.utils.RecipeDiffCallback;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.List;

/**
 * Created by miltomasz on 13/04/18.
 */

public class BakingRepository {

    private static final String LOG_TAG = BakingRepository.class.getSimpleName();

    private final RecipeDao recipeDao;
    private final IngredientDao ingredientDao;
    private final RecipeNetworkDataSource recipeNetworkDataSource;
    private final AppExecutors executors;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static BakingRepository instance;
    private boolean initialized = false;
    LiveData<List<RecipeEntry>> recipeEntries;

    private BakingRepository(RecipeDao recipeDao, IngredientDao ingredientDao,
                             RecipeNetworkDataSource recipeNetworkDataSource, AppExecutors appExecutors) {
        this.recipeDao = recipeDao;
        this.ingredientDao = ingredientDao;
        this.recipeNetworkDataSource = recipeNetworkDataSource;
        this.executors = appExecutors;

        this.recipeEntries = this.recipeDao.getRecipeEntries();

        LiveData<List<Recipe>> networkData = recipeNetworkDataSource.getRecipes();
        networkData.observeForever(new RecipesObserver());
    }

    public synchronized static BakingRepository getInstance(RecipeDao recipeDao,
                                                            IngredientDao ingredientDao,
                                                            RecipeNetworkDataSource recipeNetworkDataSource,
                                                            AppExecutors appExecutors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new BakingRepository(recipeDao, ingredientDao, recipeNetworkDataSource, appExecutors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return instance;
    }

    private synchronized void initializeData() {
        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (initialized) {
            return;
        }
        initialized = true;
        recipeNetworkDataSource.fetchRecipes();
    }

    public LiveData<List<RecipeEntry>> getRecipeEntries() {
        initializeData();
        return recipeEntries;
    }

    public LiveData<RecipeEntry> getRecipe(long id) {
        return recipeDao.getRecipeEntry(id);
    }

    public String getIngredients(long recipeId) {
        return ingredientDao.getIngredients(recipeId);
    }

    /**
     * Observes changes for recipes
     */
    private class RecipesObserver implements Observer<List<Recipe>> {
        @Override
        public void onChanged(@Nullable final List<Recipe> newRecipes) {
            executors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    final RecipeDiffCallback recipeDiffCallback =
                            new RecipeDiffCallback(recipeEntries.getValue(), newRecipes);
                    final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(recipeDiffCallback);
                    diffResult.dispatchUpdatesTo(
                            new DiffResultListUpdateCallback(recipeDao, newRecipes)
                    );
                }
            });
        }
    }

    /**
     * Calculates diff between old and new recipes
     */
    private static class DiffResultListUpdateCallback implements ListUpdateCallback {

        private RecipeDao recipeDao;
        private List<Recipe> newRecipes;

        public DiffResultListUpdateCallback(RecipeDao recipeDao, List<Recipe> newRecipes) {
            this.recipeDao = recipeDao;
            this.newRecipes = newRecipes;
        }

        @Override
        public void onInserted(int position, int count) {
            Log.d(LOG_TAG, "Values inserted. Add new recipes");
            recipeDao.bulkInsert(RecipeUtils.toEntries(newRecipes));
        }

        @Override
        public void onRemoved(int position, int count) {
            Log.d(LOG_TAG, "Values removed");
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            Log.d(LOG_TAG, "Values moved");
        }

        @Override
        public void onChanged(int position, int count, Object payload) {
            Log.d(LOG_TAG, "Values changed. Add/replace recipes if there are new ones");
            recipeDao.bulkInsert(RecipeUtils.toEntries(newRecipes));
        }
    }
}
