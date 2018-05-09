package com.plumya.bakingapp.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

/**
 * Created by miltomasz on 05/05/18.
 */

@Dao
public interface IngredientDao {

    @Query("SELECT ingredients FROM recipes WHERE id = :recipeId")
    String getIngredients(long recipeId);
}
