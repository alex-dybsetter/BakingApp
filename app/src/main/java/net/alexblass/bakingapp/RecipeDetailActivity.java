package net.alexblass.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The activity that displays the detailed information about a Recipe.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    // TODO: Need to return to detail recipe fragment on back pressed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (findViewById(R.id.fragment_container) != null) {
            // If restoring from savedInstanceState, return to avoid overlapping fragments
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

            // Get the extras from the Intent
            recipeDetailFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, recipeDetailFragment).commit();
        }
    }
}