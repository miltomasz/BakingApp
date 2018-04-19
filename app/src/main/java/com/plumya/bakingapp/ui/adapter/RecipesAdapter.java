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
import com.plumya.bakingapp.data.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private RecipesOnClickHandler clickHandler;
    private Context context;

    public RecipesAdapter(Context context, RecipesOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.recipes_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeNameTv.setText(recipe.name);
        String stepServingsCount =
                String.format("%s steps, %s servings",
                        recipe.steps == null ? "0" : recipe.steps.size() + "", recipe.servings + "");
        holder.stepsServingsTv.setText(stepServingsCount);
    }

    @Override
    public int getItemCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name)
        TextView recipeNameTv;

        @BindView(R.id.steps_servings_count)
        TextView stepsServingsTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Recipe recipe = recipes.get(position);
            clickHandler.onClick(recipe);
        }
    }

    public interface RecipesOnClickHandler {
        void onClick(Recipe recipe);
    }
}

