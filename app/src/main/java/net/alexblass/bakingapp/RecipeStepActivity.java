package net.alexblass.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * This Fragment displays the detailed information about a RecipeStep and any extra media.
 */

public class RecipeStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
    }
}