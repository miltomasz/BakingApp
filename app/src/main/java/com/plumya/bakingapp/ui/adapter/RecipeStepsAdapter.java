package com.plumya.bakingapp.ui.adapter;

/**
 * Created by miltomasz on 18/04/18.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String FAB_ENABLED = "FAB_ENABLED";

    private final int INGREDIENTS_VIEW_TYPE = 1;
    private final int REGULAR_VIEW_TYPE = 0;

    private List<Step> steps;
    private String ingredientsText;
    private RecipeStepsOnClickHandler onClickHandler;
    private Context context;
    private String ingredientsTag;

    public RecipeStepsAdapter(Context context, RecipeStepsOnClickHandler onClickHandler) {
        this.context = context;
        this.onClickHandler = onClickHandler;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }

    public void setIngredientsText(String ingredientsText) {
        this.ingredientsText = ingredientsText;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView;
        if (viewType == INGREDIENTS_VIEW_TYPE) {
            itemView = layoutInflater.inflate(
                    R.layout.recipe_steps_ingredients_list_item, parent, false);
            return new IngredientsViewHolder(itemView);
        } else {
            itemView = layoutInflater.inflate(
                    R.layout.recipe_steps_list_item, parent, false);
            return new RegularViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == INGREDIENTS_VIEW_TYPE) {
            IngredientsViewHolder ingredientsViewHolder = (IngredientsViewHolder) holder;
            ingredientsViewHolder.recipeIngredientsTv.setText(ingredientsText);
            boolean enabled = getFabDisabled() == null || !getFabDisabled().equals(ingredientsTag);
            if (enabled) {
                ingredientsViewHolder.floatingActionButton
                        .setOnClickListener(new AddToWidgetOnClickListener(ingredientsViewHolder.floatingActionButton));
                ingredientsViewHolder.floatingActionButton.setClickable(true);
                ingredientsViewHolder.floatingActionButton
                        .setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
            } else {
                ingredientsViewHolder.floatingActionButton.setOnClickListener(null);
                ingredientsViewHolder.floatingActionButton.setClickable(false);
                ingredientsViewHolder.floatingActionButton
                        .setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
        } else {
            Step step = steps.get(position - 1);
            RegularViewHolder regularViewHolder = (RegularViewHolder) holder;
            regularViewHolder.shortDescriptionTv.setText(step.shortDescription);
            regularViewHolder.fullDescriptionTv.setText(step.description);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return INGREDIENTS_VIEW_TYPE;
        }
        return REGULAR_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        return steps.size() + 1;
    }

    public void setIngredientsTag(String ingredientsTag) {
        this.ingredientsTag = ingredientsTag;
    }

    public class RegularViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.shortDescriptionTv)
        public TextView shortDescriptionTv;

        @BindView(R.id.fullDescriptionTv)
        TextView fullDescriptionTv;

        public RegularViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Step step = steps.get(position - 1);
            onClickHandler.onClick(step.id, steps);
        }
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipeIngredientsLabel)
        TextView recipeIngredientsLabelTv;

        @BindView(R.id.recipeIngredients)
        TextView recipeIngredientsTv;

        @BindView(R.id.addToWidgetBtn)
        FloatingActionButton floatingActionButton;

        public IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            floatingActionButton.setOnClickListener(new AddToWidgetOnClickListener(floatingActionButton));
        }
    }

    private String getFabDisabled() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(FAB_ENABLED, null);
    }

    public interface RecipeStepsOnClickHandler {
        void onClick(long stepId, List<Step> steps);
        void onAddToWidgetClick();
    }

    private class AddToWidgetOnClickListener implements View.OnClickListener {
        private FloatingActionButton fab;

        public AddToWidgetOnClickListener(FloatingActionButton fab) {
            this.fab = fab;
        }

        @Override
        public void onClick(View v) {
            onClickHandler.onAddToWidgetClick();
            setFabDisabled();
        }

        private void setFabDisabled() {
            fab.setOnClickListener(null);
            fab.setClickable(false);
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(FAB_ENABLED, ingredientsTag)
                    .apply();
        }
    }
}

