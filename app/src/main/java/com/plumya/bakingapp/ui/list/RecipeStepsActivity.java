package com.plumya.bakingapp.ui.list;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.model.Step;
import com.plumya.bakingapp.data.widget.IngredientsWidgetProvider;
import com.plumya.bakingapp.di.Injector;
import com.plumya.bakingapp.ui.fragments.RecipeStepDetailViewFragment;
import com.plumya.bakingapp.ui.fragments.RecipeStepsFragment;
import com.plumya.bakingapp.ui.view.RecipeStepDetailViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miltomasz on 18/04/18.
 */

public class RecipeStepsActivity extends AppCompatActivity implements RecipeStepsFragment.OnItemClickListener {

    public static final String RECIPE_ID = "recipeId";
    public static final int FIRST_ITEM = 0;
    public static final int STEP_NOT_FOUND = -1;

    private static final String LOG_TAG = RecipeStepsActivity.class.getSimpleName();

    private boolean twoPane;

    private class RecipeObserver implements Observer<Recipe> {
        private final Bundle savedInstanceState;

        public RecipeObserver(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        public void onChanged(@Nullable Recipe recipe) {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.recipe_instructions_fragment,
                                createRecipeStepDetailViewFragment(getFirstStepId(recipe.steps), recipe.steps))
                        .commit();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);

        final long recipeId = getIntent().getLongExtra(RECIPE_ID, -1L);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putLong(RECIPE_ID, recipeId);
            RecipeStepsFragment recipeStepsFragment = new RecipeStepsFragment();
            recipeStepsFragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.recipe_steps_fragment, recipeStepsFragment)
                    .commit();
        }

        if (findViewById(R.id.layout_for_recipe_details_view) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            twoPane = true;

            RecipeStepsViewModelFactory factory =
                Injector.provideRecipeStepsViewModelFactory(this.getApplicationContext());
            RecipeStepsActivityViewModel viewModel = ViewModelProviders
                    .of(this, factory).get(RecipeStepsActivityViewModel.class);
            viewModel.selectRecipeId(recipeId);
            viewModel.getRecipe().observe(this, new RecipeObserver(savedInstanceState));
        } else {
            // No more actions needed
            twoPane = false;
        }
    }

    @Override
    public void onItemSelected(long stepId, List<Step> steps) {
        if (twoPane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipe_instructions_fragment,
                            createRecipeStepDetailViewFragment(stepId, steps))
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailViewActivity.class);
            intent.putExtra(RecipeStepDetailViewActivity.STEP_ID, stepId);
            intent.putExtra(RecipeStepDetailViewActivity.STEPS, (ArrayList<Step>) steps);
            startActivity(intent);
        }
    }

    @Override
    public void onAddToWidgetClick(String recipeName) {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        addToWidget(rootView, recipeName);
    }

    public RecipeStepDetailViewFragment createRecipeStepDetailViewFragment(long stepId, List<Step> steps) {
        RecipeStepDetailViewFragment recipeStepDetailViewFragment = new RecipeStepDetailViewFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(RecipeStepDetailViewFragment.STEP_ID, stepId);
        arguments.putSerializable(RecipeStepDetailViewFragment.STEPS, (ArrayList<Step>) steps);
        recipeStepDetailViewFragment.setArguments(arguments);
        return recipeStepDetailViewFragment;
    }

    private long getFirstStepId(List<Step> steps) {
        if (steps != null && steps.size() > 0) {
            return steps.get(FIRST_ITEM).id;
        }
        return STEP_NOT_FOUND;
    }

    private void addToWidget(View view, String recipeName) {
        Log.d(LOG_TAG, "Add Recipe to widget");
        final long recipeId = getIntent().getLongExtra(RECIPE_ID, -1L);
        PreferenceManager
                .getDefaultSharedPreferences(this).edit().putLong(RECIPE_ID, recipeId)
                .apply();
        Intent intent = new Intent(this, IngredientsWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), IngredientsWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);

        String message = getString(R.string.added_to_widget, recipeName);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
