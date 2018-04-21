package com.plumya.bakingapp.ui.adapter;

/**
 * Created by miltomasz on 18/04/18.
 */

import android.content.Context;
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

    private final int INGREDIENTS_VIEW_TYPE = 1;
    private final int REGULAR_VIEW_TYPE = 0;

    private List<Step> steps;
    private String ingredientsText;
    private RecipeStepsOnClickHandler onClickHandler;
    private Context context;

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

    class RegularViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.shortDescriptionTv)
        TextView shortDescriptionTv;

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

        public IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface RecipeStepsOnClickHandler {
        void onClick(long stepId, List<Step> steps);
    }
}

