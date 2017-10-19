package net.alexblass.bakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * An Espresso test for the AddRecipeActivity.
 */

@RunWith(AndroidJUnit4.class)
public class AddRecipeActivityTest {

    @Rule
    public ActivityTestRule<AddRecipeActivity> mActivityTestRule =
            new ActivityTestRule<>(AddRecipeActivity.class);

    @Test // Test that all the fields can be edited correctly
    public void validateEditTextFields() {
        final String TYPE_TEXT_VALUE = "lorem ipsum";
        final String TYPE_INT_VALUE = "126";

        onView(withId(R.id.new_name))
                .perform(click())
                .perform(typeText(TYPE_TEXT_VALUE))
                .check(matches(withText(TYPE_TEXT_VALUE)));
        onView(withId(R.id.new_servings))
                .perform(click())
                .perform(typeText(TYPE_INT_VALUE))
                .check(matches(withText(TYPE_INT_VALUE)));

        onView(withId(R.id.add_ingredient_name))
                .perform(click())
                .perform(typeText(TYPE_TEXT_VALUE))
                .check(matches(withText(TYPE_TEXT_VALUE)));
        onView(withId(R.id.add_ingredient_quantity))
                .perform(click())
                .perform(typeText(TYPE_INT_VALUE))
                .check(matches(withText(TYPE_INT_VALUE)));
        onView(withId(R.id.add_ingredient_measurement))
                .perform(click())
                .perform(typeText(TYPE_TEXT_VALUE))
                .check(matches(withText(TYPE_TEXT_VALUE)));

        // Scroll down so the views are visible on the screen
        onView(withId(R.id.new_recipe_scrollview)).perform(swipeUp());
        onView(withId(R.id.add_step_short_desc))
                .perform(click())
                .perform(typeText(TYPE_TEXT_VALUE)).
                check(matches(withText(TYPE_TEXT_VALUE)));
        onView(withId(R.id.add_step_detail_desc))
                .perform(click())
                .perform(typeText(TYPE_TEXT_VALUE))
                .check(matches(withText(TYPE_TEXT_VALUE)));
    }

    @Test // Test that ingredients are added and removed correctly
    public void addRemoveIngredient() {
        String ingredient = "Sugar";
        String qty = "2";
        String meas = "cups";

        onView(withId(R.id.add_ingredient_name))
                .perform(click())
                .perform(typeText(ingredient));
        onView(withId(R.id.add_ingredient_quantity))
                .perform(click())
                .perform(typeText(qty));
        onView(withId(R.id.add_ingredient_measurement))
                .perform(click())
                .perform(typeText(meas));

        onView(withId(R.id.add_ingredient_btn)).perform(click());

        onView(withId(R.id.added_ingredient_name)).check(matches(withText(ingredient)));
        onView(withId(R.id.added_ingredient_qty)).check(matches(withText(qty)));
        onView(withId(R.id.added_ingredient_meas)).check(matches(withText(meas)));

        onView(withId(R.id.delete_ingredient_btn)).perform(click());

        onView(withId(R.id.added_ingredient_name)).check(doesNotExist());
        onView(withId(R.id.added_ingredient_qty)).check(doesNotExist());
        onView(withId(R.id.added_ingredient_meas)).check(doesNotExist());
    }

    @Test // Test that steps are added and removed correctly
    public void addRemoveStep() {
        String title = "Lorem ipsum";
        String description = "Lorem ipsum dolor sit amet, ipsum eu, pellentesque metus euismod tincidunt sapien, sed praesent rhoncus.";

        onView(withId(R.id.new_recipe_scrollview)).perform(swipeUp());

        onView(withId(R.id.add_step_short_desc))
                .perform(click())
                .perform(typeText(title));
        onView(withId(R.id.add_step_detail_desc))
                .perform(click())
                .perform(typeText(description));

        onView(withId(R.id.add_steps_btn)).perform(click());

        onView(withId(R.id.added_step_short_desc)).check(matches(withText(title)));
        onView(withId(R.id.added_step_detail_desc)).check(matches(withText(description)));

        onView(withId(R.id.delete_step_btn)).perform(click());

        onView(withId(R.id.added_step_short_desc)).check(doesNotExist());
        onView(withId(R.id.added_step_detail_desc)).check(doesNotExist());
    }
}
