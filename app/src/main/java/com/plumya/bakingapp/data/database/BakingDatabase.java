package com.plumya.bakingapp.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.plumya.bakingapp.data.BakingRepository;

/**
 * Created by miltomasz on 14/04/18.
 */

@Database(entities = {RecipeEntry.class}, version = 1)
public abstract class BakingDatabase extends RoomDatabase {

    private static final String LOG_TAG = BakingRepository.class.getSimpleName();
    private static final String DATABASE_NAME = "baking";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static BakingDatabase instance;

    public static BakingDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the database");
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        BakingDatabase.class, BakingDatabase.DATABASE_NAME).build();
                Log.d(LOG_TAG, "Made new database");
            }
        }
        return instance;
    }

    // The associated DAOs for the database
    public abstract RecipeDao recipeDao();
}
