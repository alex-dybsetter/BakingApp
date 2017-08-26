package net.alexblass.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.alexblass.bakingapp.models.Recipe;

import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * The activity that displays the detailed information about a Recipe.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    private Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(RECIPE_KEY)) {
                selectedRecipe = intentThatStartedThisActivity.getParcelableExtra(RECIPE_KEY);
                getSupportActionBar().setTitle(selectedRecipe.getName());
            }
        }
    }
}