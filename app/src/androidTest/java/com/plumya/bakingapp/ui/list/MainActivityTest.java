package com.plumya.bakingapp.ui.list;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.plumya.bakingapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by miltomasz on 10/05/18.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    CountingIdlingResource mainActivityIdlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mainActivityIdlingResource =
                mainActivityRule.getActivity().getEspressoIdlingResourceForMainActivity();
        Espresso.registerIdlingResources(mainActivityIdlingResource);
    }

    @After
    public void cleanUp() {
        Espresso.unregisterIdlingResources(mainActivityIdlingResource);
        mainActivityIdlingResource = null;
    }

    @Test
    public void loadRecipes_init() {
        // Given
        onView(withId(R.id.recyclerview_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Then
        onView(withId(R.id.recyclerview_recipe_steps)).check(matches(ViewMatchers.isDisplayed()));
    }
}
