package com.plumya.bakingapp.data.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.ui.list.RecipeStepsActivity;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    private static final String RECIPE_ID = "recipeId";
    private static final String LOG_TAG = IngredientsWidgetProvider.class.getSimpleName();

    public static void updateIngredientWidgets(Context context, AppWidgetManager appWidgetManager,
                                               int[] appWidgetIds, long selectedRecipeId) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, selectedRecipeId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, long selectedRecipeId) {
        // Construct the RemoteViews object
        RemoteViews views = getIngredientListView(context,  selectedRecipeId);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static RemoteViews getIngredientListView(Context context, long recipeId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, IngredientListWidgetService.class);
        intent.putExtra(RECIPE_ID, recipeId);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipeStepsActivity.class);
        appIntent.putExtra(RecipeStepsActivity.RECIPE_ID, recipeId);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);
        // Handle empty ingredients
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        long recipeId = getDefaultRecipeId(context);
        // There may be multiple widgets active, so update all of them
        updateIngredientWidgets(context, appWidgetManager, appWidgetIds, recipeId);
    }

    private long getDefaultRecipeId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(RECIPE_ID, -1);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

