package net.alexblass.bakingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;
import net.alexblass.bakingapp.utilities.RecipeQueryUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity to add new user-entered Recipes to the database.
 */

public class AddRecipeActivity extends AppCompatActivity {

    // TODO: clear all fields
    // TODO: cancel add and save recipe prompt

    // The views to get information from the user
    @BindView(R.id.new_name) EditText mRecipeName;
    @BindView(R.id.new_servings) EditText mServings;
    @BindView(R.id.add_ingredient_name) EditText mIngredientName;
    @BindView(R.id.add_ingredient_quantity) EditText mIngredientQty;
    @BindView(R.id.add_ingredient_measurement) EditText mIngredientMeas;
    @BindView(R.id.add_step_short_desc) EditText mStepShortDesc;
    @BindView(R.id.add_step_detail_desc) EditText mStepDetailDesc;
    @BindView(R.id.add_ingredient_btn) Button mAddIngredientBtn;
    @BindView(R.id.add_steps_btn) Button mAddStepBtn;
    @BindView(R.id.add_btn) Button mAddRecipeBtn;
    @BindView(R.id.ingredient_container) LinearLayout mIngredientContainer;
    @BindView(R.id.step_container) LinearLayout mStepContainer;

    // The variables to store the information provided by the user
    private String mRecipeNameString;
    private int mServingsInt;

    private List<Ingredient> mIngredientsList;

    private List<RecipeStep> mStepsList;

    private int mDbId;
    // Assign a dummy value to the field for the API ID since these recipes are not from the API
    private final int mId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        ButterKnife.bind(this);

        mIngredientsList = new ArrayList<>();
        mAddIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }});

        mStepsList = new ArrayList<>();
        mAddStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStep();
            }});

        mAddRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });
    }

    // Verify the fields have data and then create our recipe with the data and save it to the DB
    private void saveRecipe(){
        if (TextUtils.isEmpty(mRecipeName.getText().toString().trim())){
            String prompt = getString(R.string.incomplete_recipe_prompt, mRecipeName.getHint().toString());
            showErrorDialog(prompt);
            return;
        } else {
            mRecipeNameString = mRecipeName.getText().toString().trim();
        }

        if (TextUtils.isEmpty(mServings.getText().toString().trim())){
            String prompt = getString(R.string.incomplete_recipe_prompt, mServings.getHint().toString());
            showErrorDialog(prompt);
            return;
        }else {
            mServingsInt = Integer.parseInt(mServings.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(mIngredientName.getText().toString().trim()) &&
                !TextUtils.isEmpty(mIngredientQty.getText().toString().trim()) &&
                !TextUtils.isEmpty(mIngredientMeas.getText().toString().trim())) {

            mIngredientsList.add(new Ingredient(
                    Long.parseLong(mIngredientQty.getText().toString().trim()),
                    mIngredientMeas.getText().toString().trim(),
                    mIngredientName.getText().toString().trim()
            ));
        }

        if (!TextUtils.isEmpty(mStepShortDesc.getText().toString().trim()) &&
                !TextUtils.isEmpty(mStepDetailDesc.getText().toString().trim())) {

            // At this time, pass in an empty string for image and video URLs
            mStepsList.add(new RecipeStep(
                    mStepsList.size(),
                    mStepShortDesc.getText().toString().trim(),
                    mStepDetailDesc.getText().toString().trim(),
                    "",
                    ""
            ));
        }

        // Add a recipe to the database.
        // Pass in an empty string for the image URL
        // Pass in default false for favorites
        // Pass in -1 for ID since this recipe is not from the API and therefore has no pre existing
        // ID and has not yet been assigned a DB ID.
        Recipe recipe = new Recipe(mId, mRecipeNameString, mIngredientsList, mStepsList,
                mServingsInt, "", false, mId);
        long recipeId = RecipeQueryUtils.addRecipe(this, recipe, false);

        // Add the Recipe Ingredients
        for (Ingredient i : recipe.getIngredients()) {
            RecipeQueryUtils.addIngredient(this, i, recipeId);
        }

        // Add the RecipeSteps
        for (RecipeStep step : recipe.getSteps()) {
            RecipeQueryUtils.addStep(this, step, recipeId);
        }

        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        finish();
    }

    // Add another ingredient
    private void addIngredient(){
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.item_added_ingredient, null);

        if (!TextUtils.isEmpty(mIngredientName.getText().toString().trim()) &&
                !TextUtils.isEmpty(mIngredientQty.getText().toString().trim()) &&
                !TextUtils.isEmpty(mIngredientMeas.getText().toString().trim())) {

            TextView name = (TextView) addView.findViewById(R.id.added_ingredient_name);
            name.setText(mIngredientName.getText().toString().trim());

            TextView qty = (TextView) addView.findViewById(R.id.added_ingredient_qty);
            qty.setText(mIngredientQty.getText().toString().trim());

            TextView meas = (TextView) addView.findViewById(R.id.added_ingredient_meas);
            meas.setText(mIngredientMeas.getText().toString().trim());

            mIngredientsList.add(new Ingredient(
                    Long.parseLong(mIngredientQty.getText().toString().trim()),
                    mIngredientMeas.getText().toString().trim(),
                    mIngredientName.getText().toString().trim()
            ));

            mIngredientName.setText("");
            mIngredientQty.setText("");
            mIngredientMeas.setText("");


            Button buttonRemove = (Button) addView.findViewById(R.id.delete_ingredient_btn);
            buttonRemove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mIngredientsList.remove(mIngredientContainer.indexOfChild(addView));
                    ((LinearLayout) addView.getParent()).removeView(addView);
                }
            });

            mIngredientContainer.addView(addView);
        } else {
            showErrorDialog(getString(R.string.incomplete_ingredient_prompt));
        }
    }

    // Add another recipe step
    private void addStep(){
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.item_added_step, null);

        if (!TextUtils.isEmpty(mStepShortDesc.getText().toString().trim()) &&
                !TextUtils.isEmpty(mStepDetailDesc.getText().toString().trim())) {

            TextView shortDesc = (TextView) addView.findViewById(R.id.added_step_short_desc);
            shortDesc.setText(mStepShortDesc.getText().toString().trim());

            TextView detailDesc = (TextView) addView.findViewById(R.id.added_step_detail_desc);
            detailDesc.setText(mStepDetailDesc.getText().toString().trim());

            // At this time, pass in empty strings for image and video URLs
            mStepsList.add(new RecipeStep(
                    mStepsList.size(),
                    mStepShortDesc.getText().toString().trim(),
                    mStepDetailDesc.getText().toString().trim(),
                    "",
                    ""
            ));

            mStepShortDesc.setText("");
            mStepDetailDesc.setText("");

            Button buttonRemove = (Button) addView.findViewById(R.id.delete_step_btn);
            buttonRemove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mStepsList.remove(mStepContainer.indexOfChild(addView));
                    for (int i = 0; i < mStepsList.size(); i++){
                        mStepsList.get(i).setId(i);
                    }
                    ((LinearLayout) addView.getParent()).removeView(addView);
                }
            });

            mStepContainer.addView(addView);
        }else {
            showErrorDialog(getString(R.string.incomplete_step_prompt));
        }
    }

    private void showErrorDialog(String prompt){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.incomplete_data_title))
                .setMessage(prompt);

        dialog.setPositiveButton(R.string.positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.create().show();
    }
}
