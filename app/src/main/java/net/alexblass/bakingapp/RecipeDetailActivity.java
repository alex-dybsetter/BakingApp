package net.alexblass.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The activity that displays the detailed information about a Recipe.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
    }
}