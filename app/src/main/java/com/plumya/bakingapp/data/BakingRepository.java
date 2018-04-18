package com.plumya.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.plumya.bakingapp.data.database.RecipeDao;
import com.plumya.bakingapp.data.database.RecipeEntry;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.network.RecipeNetworkDataSource;
import com.plumya.bakingapp.utils.AppExecutors;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.List;

/**
 * Created by miltomasz on 13/04/18.
 */

public class BakingRepository {

    private static final String LOG_TAG = BakingRepository.class.getSimpleName();

    private final RecipeDao recipeDao;
    private final RecipeNetworkDataSource recipeNetworkDataSource;
    private final AppExecutors executors;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static BakingRepository instance;
    private boolean initialized = false;

    private BakingRepository(final RecipeDao recipeDao,
                             RecipeNetworkDataSource recipeNetworkDataSource, AppExecutors appExecutors) {
        this.recipeDao = recipeDao;
        this.recipeNetworkDataSource = recipeNetworkDataSource;
        this.executors = appExecutors;

        LiveData<List<Recipe>> networkData = recipeNetworkDataSource.getRecipes();
        networkData.observeForever(new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable final List<Recipe> recipes) {
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        recipeDao.deleteAllRecipes();
                        Log.d(LOG_TAG, "Old recipes deleted");
                        recipeDao.bulkInsert(RecipeUtils.toEntries(recipes));
                        Log.d(LOG_TAG, "New values inserted");
                    }
                });
            }
        });
    }

    public synchronized static BakingRepository getInstance(RecipeDao recipeDao,
                                                            RecipeNetworkDataSource recipeNetworkDataSource,
                                                            AppExecutors appExecutors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new BakingRepository(recipeDao, recipeNetworkDataSource, appExecutors);
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


    public LiveData<List<RecipeEntry>> getRecipes() {
        initializeData();
        return recipeDao.getRecipes();
    }
}
