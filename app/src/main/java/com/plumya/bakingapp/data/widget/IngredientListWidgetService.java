package com.plumya.bakingapp.data.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.BakingRepository;
import com.plumya.bakingapp.data.model.Ingredient;
import com.plumya.bakingapp.di.Injector;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.List;

import static com.plumya.bakingapp.data.widget.IngredientListRemoteViewsFactory.*;

/**
 * Created by miltomasz on 05/05/18.
 */

public class IngredientListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        long selectedRecipeId = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()).getLong(RECIPE_ID, DEF_VALUE);
        return new IngredientListRemoteViewsFactory(this.getApplicationContext(), selectedRecipeId);
    }
}

class IngredientListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String RECIPE_ID = "recipeId";
    public static final int DEF_VALUE = -1;

    Context context;
    BakingRepository repository;
    List<Ingredient> ingredients;
    long recipeId;

    public IngredientListRemoteViewsFactory(Context applicationContext, long selectedRecipeId) {
        this.context = applicationContext;
        this.repository = Injector.provideBakingRespository(context);
        this.recipeId = selectedRecipeId;
    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get ingredients for a given recipe
        String ingredientsString = repository.getIngredients(getRecipeId());
        ingredients = RecipeUtils.toIngredients(ingredientsString);
    }

    @Override
    public void onDestroy() {
        ingredients = null;
    }

    @Override
    public int getCount() {
        if (ingredients == null) return 0;
        return ingredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients == null || ingredients.size() == 0) return null;
        Ingredient ingredient = ingredients.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_view_item);

        setTextsToViews(ingredient, views);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putLong(RECIPE_ID, getRecipeId());

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        setFillIntentToViews(views, fillInIntent);
        return views;

    }

    private long getRecipeId() {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(RECIPE_ID, DEF_VALUE);
    }

    private void setTextsToViews(Ingredient ingredient, RemoteViews views) {
        views.setTextViewText(R.id.ingredient_tv, ingredient.ingredient);
        views.setTextViewText(R.id.quantity_tv, String.valueOf(ingredient.quantity));
        views.setTextViewText(R.id.measure_tv, ingredient.measure);
    }

    private void setFillIntentToViews(RemoteViews views, Intent fillInIntent) {
        views.setOnClickFillInIntent(R.id.main_layout_view_item, fillInIntent);
        views.setOnClickFillInIntent(R.id.ingredient_tv, fillInIntent);
        views.setOnClickFillInIntent(R.id.quantity_tv, fillInIntent);
        views.setOnClickFillInIntent(R.id.measure_tv, fillInIntent);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
