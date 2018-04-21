package com.plumya.bakingapp.ui.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.model.Step;
import com.plumya.bakingapp.di.Injector;
import com.plumya.bakingapp.ui.adapter.RecipeStepsAdapter;
import com.plumya.bakingapp.ui.view.RecipeStepDetailViewActivity;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 18/04/18.
 */

public class RecipeStepsActivity extends AppCompatActivity implements RecipeStepsAdapter.RecipeStepsOnClickHandler {

    public static final String RECIPE_ID = "recipeId";
    private static final String LOG_TAG = RecipeStepsActivity.class.getSimpleName();

    private RecipeStepsAdapter recipeStepsAdapter;
    private RecipeStepsActivityViewModel viewModel;

    @BindView(R.id.recyclerview_recipe_steps)
    RecyclerView recipesRv;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;

    @BindView(R.id.tv_error_message_display)
    TextView emptyErrorTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        ButterKnife.bind(this);

        initializeRecyclerView();

        showProgressBar(true);

        RecipeStepsViewModelFactory factory =
                Injector.provideRecipeStepsViewModelFactory(this.getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(RecipeStepsActivityViewModel.class);

        long recipeId = getIntent().getLongExtra(RECIPE_ID, -1L);
        viewModel.selectRecipeId(recipeId);

        viewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (noRecipeSteps(recipe)) {
                    showEmptyTextView();
                } else {
                    showRecyclerView();
                    recipeStepsAdapter.setSteps(recipe.steps);
                    recipeStepsAdapter.setIngredientsText(
                            RecipeUtils.displayIngredients(RecipeStepsActivity.this, recipe)
                    );
                }
                showProgressBar(false);
            }

            private boolean noRecipeSteps(@Nullable Recipe recipe) {
                return recipe == null || recipe.steps == null || recipe.steps.size() == 0;
            }
        });
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyTextView() {
        emptyErrorTv.setVisibility(View.VISIBLE);
        recipesRv.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        emptyErrorTv.setVisibility(View.GONE);
        recipesRv.setVisibility(View.VISIBLE);
    }

    private void initializeRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipesRv.setLayoutManager(layoutManager);
        recipesRv.setHasFixedSize(true);
        recipesRv.setDrawingCacheEnabled(true);
        recipeStepsAdapter = new RecipeStepsAdapter(this, RecipeStepsActivity.this);
        recipesRv.setAdapter(recipeStepsAdapter);
        recipesRv.setSaveEnabled(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recipesRv.getContext(),
                layoutManager.getOrientation());
        recipesRv.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onClick(long stepId, List<Step> steps) {
        Intent intent = new Intent(this, RecipeStepDetailViewActivity.class);
        intent.putExtra(RecipeStepDetailViewActivity.STEP_ID, stepId);
        intent.putExtra(RecipeStepDetailViewActivity.STEPS, (ArrayList<Step>) steps);
        startActivity(intent);
    }
}
