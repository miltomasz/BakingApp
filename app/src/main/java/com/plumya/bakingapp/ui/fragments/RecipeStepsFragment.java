package com.plumya.bakingapp.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.data.model.Step;
import com.plumya.bakingapp.di.Injector;
import com.plumya.bakingapp.ui.adapter.RecipeStepsAdapter;
import com.plumya.bakingapp.ui.list.RecipeStepsActivity;
import com.plumya.bakingapp.ui.list.RecipeStepsActivityViewModel;
import com.plumya.bakingapp.ui.list.RecipeStepsViewModelFactory;
import com.plumya.bakingapp.ui.view.RecipeStepDetailViewActivity;
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 24/04/18.
 */

public class RecipeStepsFragment extends Fragment implements RecipeStepsAdapter.RecipeStepsOnClickHandler{

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

    private long recipeId;

    public RecipeStepsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);

        ButterKnife.bind(this, rootView);

        initializeRecyclerView();

        showProgressBar(true);

        RecipeStepsViewModelFactory factory =
                Injector.provideRecipeStepsViewModelFactory(getActivity().getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(RecipeStepsActivityViewModel.class);

        Bundle arguments = getArguments();
        long recipeId = arguments.getLong(RECIPE_ID, -1L);
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
                            RecipeUtils.displayIngredients(getActivity(), recipe)
                    );
                }
                showProgressBar(false);
            }

            private boolean noRecipeSteps(@Nullable Recipe recipe) {
                return recipe == null || recipe.steps == null || recipe.steps.size() == 0;
            }
        });

        // Return the root view
        return rootView;
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recipesRv.setLayoutManager(layoutManager);
        recipesRv.setHasFixedSize(true);
        recipesRv.setDrawingCacheEnabled(true);
        recipeStepsAdapter = new RecipeStepsAdapter(getActivity(), this);
        recipesRv.setAdapter(recipeStepsAdapter);
        recipesRv.setSaveEnabled(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recipesRv.getContext(),
                layoutManager.getOrientation());
        recipesRv.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onClick(long stepId, List<Step> steps) {
        Intent intent = new Intent(getActivity(), RecipeStepDetailViewActivity.class);
        intent.putExtra(RecipeStepDetailViewActivity.STEP_ID, stepId);
        intent.putExtra(RecipeStepDetailViewActivity.STEPS, (ArrayList<Step>) steps);
        startActivity(intent);
    }
}
