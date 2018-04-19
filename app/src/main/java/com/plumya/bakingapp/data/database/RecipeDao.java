package com.plumya.bakingapp.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by miltomasz on 13/04/18.
 */

@Dao
public interface RecipeDao {

    @Query("SELECT id, name, ingredients, steps, servings, image FROM recipes")
    LiveData<List<RecipeEntry>> getRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    LiveData<RecipeEntry> getRecipe(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(RecipeEntry... recipes);

    @Query("DELETE FROM recipes")
    void deleteAllRecipes();
}
