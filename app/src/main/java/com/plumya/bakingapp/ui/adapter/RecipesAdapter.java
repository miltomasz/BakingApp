package com.plumya.bakingapp.ui.adapter;

/**
 * Created by miltomasz on 18/04/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Recipe;
import com.plumya.bakingapp.utils.VideoUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    public static final String ZERO = "0";
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
        String stepsCount = recipe.steps == null ? ZERO : String.valueOf(recipe.steps.size());
        String servingsCount = String.valueOf(recipe.servings);
        String stepServingsCount = context.getString(R.string.steps_servings, stepsCount, servingsCount);
        holder.stepsServingsTv.setText(stepServingsCount);
        if (TextUtils.isEmpty(recipe.image)) {
            int resourceId = VideoUtil.getDefaultRecipeImage(recipe.name);
            holder.recipeImageView.setImageResource(resourceId);
        } else {
            Picasso.get()
                    .load(recipe.image)
                    .placeholder(R.drawable.question_mark)
                    .error(R.drawable.recipe_placeholder)
                    .into(holder.recipeImageView);
        }
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

        @BindView(R.id.imageView)
        ImageView recipeImageView;

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

