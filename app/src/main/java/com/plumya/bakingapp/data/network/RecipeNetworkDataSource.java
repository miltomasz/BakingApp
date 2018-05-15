package com.plumya.bakingapp.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.plumya.bakingapp.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by miltomasz on 17/04/18.
 */

public class RecipeNetworkDataSource {

    private static final String LOG_TAG = RecipeNetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static RecipeNetworkDataSource instance;
    // Retrofit service
    private final BakingService bakingService;
    // LiveData storing the latest downloaded recipes
    private final MutableLiveData<List<Recipe>> downloadedRecipes;

    private RecipeNetworkDataSource(BakingService bakingService) {
        this.bakingService = bakingService;
        this.downloadedRecipes = new MutableLiveData<>();
    }

    public static RecipeNetworkDataSource getInstance(BakingService bakingService) {
        Log.d(LOG_TAG, "Getting the Recipe network data source");
        if (instance == null) {
            synchronized (LOCK) {
                instance = new RecipeNetworkDataSource(bakingService);
                Log.d(LOG_TAG, "Made new Recipe network data source");
            }
        }
        return instance;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return downloadedRecipes;
    }

    public void fetchRecipes() {
        Log.d(LOG_TAG, "Fetching recipes from network");
        bakingService.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                downloadedRecipes.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.d(LOG_TAG, "Request failed: " + t.getMessage());
            }
        });
    }
}
