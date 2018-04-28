package com.plumya.bakingapp.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.plumya.bakingapp.utils.RecipeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 24/04/18.
 */

public class RecipeStepsFragment extends Fragment implements RecipeStepsAdapter.RecipeStepsOnClickHandler {

    private static final String LOG_TAG = RecipeStepsActivity.class.getSimpleName();

    public static final String RECIPE_ID = "recipeId";

    private RecipeStepsAdapter recipeStepsAdapter;
    private RecipeStepsActivityViewModel viewModel;

    @BindView(R.id.recyclerview_recipe_steps)
    RecyclerView recipesRv;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;

    @BindView(R.id.tv_error_message_display)
    TextView emptyErrorTv;

    private OnItemClickListener callback;

    public interface OnItemClickListener {
        void onItemSelected(long stepId, List<Step> steps);
    }

    private class RecipeObserver implements Observer<Recipe> {
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
    }

    public RecipeStepsFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            callback = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
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
        viewModel.getRecipe().observe(this, new RecipeObserver());

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
        callback.onItemSelected(stepId, steps);
    }
}
