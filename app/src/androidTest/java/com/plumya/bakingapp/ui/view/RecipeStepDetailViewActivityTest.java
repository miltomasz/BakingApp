package com.plumya.bakingapp.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.ui.list.RecipeStepsActivity;
import com.plumya.bakingapp.util.RecyclerViewMatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Created by miltomasz on 13/05/18.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeStepDetailViewActivityTest {

    @Rule
    public ActivityTestRule<RecipeStepsActivity> recipeStepsActivityRule =
            new ActivityTestRule<RecipeStepsActivity>(RecipeStepsActivity.class, true, true) {

                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent intent = new Intent(targetContext, RecipeStepsActivity.class);
                    intent.putExtra(RecipeStepsActivity.RECIPE_ID, 2L);
                    return intent;
                }
            };

    @Test
    public void selectRecipeStep() {
        onView(withRecyclerView(R.id.recyclerview_recipe_steps).atPosition(2))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.recipeStepInstructionTv))
                .check(matches(withText(containsString("Preheat the oven to 350"))));
    }

    @Test
    public void selectRecipeStepNext() {
        onView(withRecyclerView(R.id.recyclerview_recipe_steps).atPosition(2))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.nextBtn))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.recipeStepInstructionTv))
                .check(matches(withText(containsString("Melt the butter and bittersweet"))));
    }

    @Test
    public void selectRecipeStepBack() {
        onView(withRecyclerView(R.id.recyclerview_recipe_steps).atPosition(2))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.backBtn))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.recipeStepInstructionTv))
                .check(matches(withText("Recipe Introduction")));
    }

    /**
     * Helper for operating on recycler views
     * @param recyclerViewId
     * @return
     */
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
