package com.plumya.bakingapp.ui.list;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.ui.view.RecipeStepDetailViewActivity;
import com.plumya.bakingapp.util.RecyclerViewMatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

import static org.hamcrest.core.StringContains.containsString;

/**
 * Created by miltomasz on 12/05/18.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeStepsActivityTest {

    public static final long RECIPE_2_IN_ROW = 2L;

    @Rule
    public IntentsTestRule<RecipeStepsActivity> recipeStepsActivityRule =
            new IntentsTestRule<RecipeStepsActivity>(RecipeStepsActivity.class, true, true) {

                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent intent = new Intent(targetContext, RecipeStepsActivity.class);
                    intent.putExtra(RecipeStepsActivity.RECIPE_ID, RECIPE_2_IN_ROW);
                    return intent;
                }
            };

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void recipeStepsAreInitialized() {
        // check first row initialization
        onView(withRecyclerView(R.id.recyclerview_recipe_steps).atPosition(0))
                .check(matches(
                        hasDescendant(withText(containsString("Bittersweet chocolate (60-70% cacao)"))))
                );

        // check first row initialization
        onView(withRecyclerView(R.id.recyclerview_recipe_steps).atPosition(1))
                .check(matches(
                        hasDescendant(withText("Recipe Introduction")))
                );
    }

    @Test
    public void intentSentOnRowClick() {
        // tap on second row
        onView(withRecyclerView(R.id.recyclerview_recipe_steps).atPosition(2))
                .check(matches(isDisplayed()))
                .perform(click());

        intended(allOf(
                hasComponent(RecipeStepDetailViewActivity.class.getName()),
                hasExtra(RecipeStepDetailViewActivity.STEP_ID, 1L))
        );
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
