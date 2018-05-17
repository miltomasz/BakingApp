package com.plumya.bakingapp.ui.list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.plumya.bakingapp.data.BakingRepository;

/**
 * Created by miltomasz on 18/04/18.
 */

public class RecipeStepsViewModelFactory  extends ViewModelProvider.NewInstanceFactory {

    private final BakingRepository bakingRepository;

    public RecipeStepsViewModelFactory(BakingRepository repository) {
        this.bakingRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new RecipeStepsActivityViewModel(bakingRepository);
    }
}
