package com.plumya.bakingapp.ui.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.di.Injector;
import com.plumya.bakingapp.ui.adapter.RecipesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipesOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int CACHE_SIZE = 20;

    private MainActivityViewModel viewModel;
    private RecipesAdapter recipesAdapter;

    @BindView(R.id.recyclerview_recipes)
    RecyclerView recipesRv;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;

    @BindView(R.id.tv_error_message_display)
    TextView emptyErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeRecyclerView();
        showProgressBar(true);

        MainViewModelFactory factory =
                Injector.provideMainActivityViewModelFactory(this.getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d(LOG_TAG, "Retrieved recipes: " + recipes);
                if (recipes == null || recipes.size() == 0) {
                    showEmptyError();
                } else {
                    showRecyclerView();
                }
                recipesAdapter.setRecipes(recipes);
                showProgressBar(false);
            }
        });
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyError() {
        emptyErrorTv.setVisibility(View.VISIBLE);
        recipesRv.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        emptyErrorTv.setVisibility(View.GONE);
        recipesRv.setVisibility(View.VISIBLE);
    }

    private void initializeRecyclerView() {
        recipesRv.setLayoutManager(new LinearLayoutManager(this));
        recipesRv.setHasFixedSize(true);
        recipesRv.setItemViewCacheSize(CACHE_SIZE);
        recipesRv.setDrawingCacheEnabled(true);
        recipesAdapter = new RecipesAdapter(this, MainActivity.this);
        recipesRv.setAdapter(recipesAdapter);
        recipesRv.setSaveEnabled(true);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeStepsActivity.class);
        intent.putExtra(RecipeStepsActivity.RECIPE_ID, recipe.id);
        startActivity(intent);
    }
}
