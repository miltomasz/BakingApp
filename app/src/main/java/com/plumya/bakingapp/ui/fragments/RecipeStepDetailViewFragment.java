package com.plumya.bakingapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Step;

import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by miltomasz on 24/04/18.
 */

public class RecipeStepDetailViewFragment extends Fragment {

    private static final String LOG_TAG = RecipeStepDetailViewFragment.class.getSimpleName();

    public static final String STEP_ID = "stepId";
    public static final String STEPS = "steps";

    private long stepId;
    private List<Step> steps;
    private ListIterator<Step> iterator;

    @Nullable
    @BindView(R.id.recipeStepInstructionTv)
    TextView recipeStepInstructionTv;

    @BindView(R.id.videoView)
    VideoView videoView;

    public RecipeStepDetailViewFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_recipe_step_detail_view, container, false);
        ButterKnife.bind(this, rootView);
        if (steps != null) {
            initializeIterator();
            setIteratorCursor();
        }
        // Return the root view
        return rootView;
    }

    private void initializeIterator() {
        iterator = steps.listIterator();
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void selectedStep(long stepId, List<Step> steps) {
        this.stepId = stepId;
        this.steps = steps;
        initializeIterator();
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

    @Optional
    @OnClick(R.id.backBtn)
    public void onBack() {
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

    @Optional
    @OnClick(R.id.nextBtn)
    public void onNext() {
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
        if (recipeStepInstructionTv != null) {
            recipeStepInstructionTv.setText(instructions);
        }
    }
}
