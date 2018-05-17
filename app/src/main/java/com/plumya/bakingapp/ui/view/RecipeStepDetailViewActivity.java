package com.plumya.bakingapp.ui.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Step;
import com.plumya.bakingapp.ui.fragments.RecipeStepDetailViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miltomasz on 20/04/18.
 */

public class RecipeStepDetailViewActivity extends AppCompatActivity {

    public static final String STEP_ID = "stepId";
    public static final String STEPS = "steps";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail_view);

        Bundle arguments = new Bundle();
        Intent intent = getIntent();

        if (intent.hasExtra(STEP_ID)) {
            long stepId = intent.getLongExtra(STEP_ID, -1);
            List<Step> steps = (List<Step>) intent.getSerializableExtra(STEPS);
            arguments.putLong(STEP_ID, stepId);
            arguments.putSerializable(STEPS, (ArrayList<Step>) steps);
        } else {
            Toast
                    .makeText(this, R.string.problem_loading_step_msg, Toast.LENGTH_SHORT)
                    .show();
        }

        if (savedInstanceState == null) {
            RecipeStepDetailViewFragment recipeStepDetailViewFragment = new RecipeStepDetailViewFragment();
            recipeStepDetailViewFragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.recipe_step_detail_view_fragment, recipeStepDetailViewFragment)
                    .commit();
        }
        hideToolbarIfLandscape();
    }

    private void hideToolbarIfLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        }
    }
}
