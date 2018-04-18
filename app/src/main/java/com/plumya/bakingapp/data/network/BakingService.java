package com.plumya.bakingapp.data.network;

import com.plumya.bakingapp.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by miltomasz on 15/04/18.
 */

public interface BakingService {

    @Headers("Content-Type: application/json")
    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();
}
