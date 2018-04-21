package com.plumya.bakingapp.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Step;

import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 20/04/18.
 */

public class RecipeStepDetailViewActivity extends AppCompatActivity {

    public static final String STEP_ID = "stepId";
    public static final String STEPS = "steps";

    private long stepId;
    private List<Step> steps;
    private ListIterator<Step> iterator;
    private Step step;

    @BindView(R.id.recipeStepInstructionTv)
    TextView recipeStepInstructionTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail_view);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(STEP_ID)) {
            stepId = intent.getLongExtra(STEP_ID, 0);
            steps = (List<Step>) intent.getSerializableExtra(STEPS);
        } else {
            Toast
                    .makeText(this, R.string.problem_loading_step_msg, Toast.LENGTH_SHORT)
                    .show();
        }
        iterator = steps.listIterator();
        setIteratorCursor();
    }

    private void setIteratorCursor() {
        while (iterator.hasNext()) {
            Step step = iterator.next();
            if (stepId == step.id) {
                setStepInstructions(step);
                stepId = step.id;
                break;
            }
        }
    }

    public void onBack(View view) {
        if (iterator.hasPrevious()) {
            Step step = iterator.previous();
            if (step.id == stepId) {
                if (iterator.hasPrevious()) {
                    step = iterator.previous();
                }
            }
            setStepInstructions(step);
            stepId = step.id;
        }
    }

    public void onNext(View view) {
        if (iterator.hasNext()) {
            Step step = iterator.next();
            if (step.id == stepId) {
                if (iterator.hasNext()) {
                    step = iterator.next();
                }
            }
            setStepInstructions(step);
            stepId = step.id;
        }
    }

    private void setStepInstructions(Step step) {
        String instructions = step.description;
        recipeStepInstructionTv.setText(instructions);
    }
}
