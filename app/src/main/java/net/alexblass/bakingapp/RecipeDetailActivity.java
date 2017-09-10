package net.alexblass.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * The activity that displays the detailed information about a Recipe.
 */

public class RecipeDetailActivity extends AppCompatActivity {

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
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, recipeDetailFragment, "recipeDetailFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    // Override method to determine action on Up button pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // On Up button click, return to previous Fragment or MainActivity
            case android.R.id.home:
                // getBackStackEntryCount must be greater than 1 so that we don't end up with
                // an empty Fragment screen.
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Override method to make sure we don't end up with an empty Fragment screen on Back clicked
    @Override
    public void onBackPressed() {
        // If there's only one Fragment in our stack, then it's an empty Fragment we can skip
        // and return to the parent Activity.
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}