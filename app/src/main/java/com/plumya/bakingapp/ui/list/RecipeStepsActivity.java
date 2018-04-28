package com.plumya.bakingapp.ui.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.model.Step;
import com.plumya.bakingapp.di.Injector;
import com.plumya.bakingapp.ui.fragments.RecipeStepDetailViewFragment;
import com.plumya.bakingapp.ui.fragments.RecipeStepsFragment;
import com.plumya.bakingapp.ui.view.RecipeStepDetailViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by miltomasz on 18/04/18.
 */

public class RecipeStepsActivity extends AppCompatActivity implements RecipeStepsFragment.OnItemClickListener {

    public static final String RECIPE_ID = "recipeId";
    private static final String LOG_TAG = RecipeStepsActivity.class.getSimpleName();
    public static final int STEP_NOT_FOUND = -1;

    private boolean twoPane;

    @BindView(R.id.recyclerview_recipe_steps)
    RecyclerView recipesRv;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;

    @BindView(R.id.tv_error_message_display)
    TextView emptyErrorTv;

    private class RecipeObserver implements Observer<Recipe> {
        private final Bundle savedInstanceState;

        public RecipeObserver(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        public void onChanged(@Nullable Recipe recipe) {
            if (savedInstanceState == null) {
                RecipeStepDetailViewFragment recipeStepDetailViewFragment = new RecipeStepDetailViewFragment();
                recipeStepDetailViewFragment.selectedStep(getFirstStepId(recipe.steps), recipe.steps);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.recipe_instructions_fragment, recipeStepDetailViewFragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);

        final long recipeId = getIntent().getLongExtra(RECIPE_ID, -1L);

        if(savedInstanceState == null) {
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

    private long getFirstStepId(List<Step> steps) {
        if (steps != null && steps.size() > 0) {
            return steps.get(0).id;
        }
        return STEP_NOT_FOUND;
    }

    @Override
    public void onItemSelected(long stepId, List<Step> steps) {
        if (twoPane) {
            RecipeStepDetailViewFragment recipeStepDetailViewFragment = new RecipeStepDetailViewFragment();
            recipeStepDetailViewFragment.selectedStep(stepId, steps);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipe_instructions_fragment, recipeStepDetailViewFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailViewActivity.class);
            intent.putExtra(RecipeStepDetailViewActivity.STEP_ID, stepId);
            intent.putExtra(RecipeStepDetailViewActivity.STEPS, (ArrayList<Step>) steps);
            startActivity(intent);
        }
    }
}
