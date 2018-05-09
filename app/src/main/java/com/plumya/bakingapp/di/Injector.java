package com.plumya.bakingapp.di;

import android.content.Context;

import com.plumya.bakingapp.data.BakingRepository;
import com.plumya.bakingapp.data.database.BakingDatabase;
import com.plumya.bakingapp.data.database.IngredientDao;
import com.plumya.bakingapp.data.database.RecipeDao;
import com.plumya.bakingapp.data.network.BakingService;
import com.plumya.bakingapp.data.network.RecipeNetworkDataSource;
import com.plumya.bakingapp.ui.list.MainViewModelFactory;
import com.plumya.bakingapp.ui.list.RecipeStepsViewModelFactory;
import com.plumya.bakingapp.utils.AppExecutors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by miltomasz on 13/04/18.
 */

public class Injector {

    public static final String API_BASE_URL = "https://d17h27t6h515a5.cloudfront.net";
    private static Retrofit retrofit;

    private Injector(){}

    public static Retrofit provideRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static RecipeNetworkDataSource provideNetworkDataSource(Context context) {
        Retrofit retrofit = provideRetrofit();
        BakingService bakingService = provideBakingService(retrofit);
        return RecipeNetworkDataSource.getInstance(bakingService);
    }

    public static BakingService provideBakingService(Retrofit retrofit) {
        return retrofit.create(BakingService.class);
    }

    public static BakingRepository provideBakingRespository(Context context) {
        BakingDatabase bakingDatabase = BakingDatabase.getInstance(context);
        RecipeNetworkDataSource recipeNetworkDataSource = provideNetworkDataSource(context);
        AppExecutors executors = AppExecutors.getInstance();
        RecipeDao recipeDao = bakingDatabase.recipeDao();
        IngredientDao ingredientDao = bakingDatabase.ingredientDao();
        return BakingRepository.getInstance(recipeDao, ingredientDao, recipeNetworkDataSource, executors);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        BakingRepository bakingRepository = provideBakingRespository(context.getApplicationContext());
        return new MainViewModelFactory(bakingRepository);
    }

    public static RecipeStepsViewModelFactory provideRecipeStepsViewModelFactory(Context context) {
        BakingRepository bakingRepository = provideBakingRespository(context.getApplicationContext());
        return new RecipeStepsViewModelFactory(bakingRepository);
    }
}
