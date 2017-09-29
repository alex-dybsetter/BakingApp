package net.alexblass.bakingapp.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import net.alexblass.bakingapp.data.IngredientsContract.IngredientEntry;
import net.alexblass.bakingapp.data.RecipeStepsContract.RecipeStepEntry;
import net.alexblass.bakingapp.data.RecipesContract.RecipeEntry;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;

import java.util.ArrayList;
import java.util.List;


/**
 * Loads a list of Recipes using the AsyncTask
 * to perform the network request to the JSON URL.
 */

public class RecipeLoader extends AsyncTaskLoader<Recipe[]> {
    // The query URL
    private String mUrl = null;

    // Constructs a new RecipeLoader
    public RecipeLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Recipe[] loadInBackground() {
        Recipe[] recipes = null;

        if (mUrl == null){

            String[] projection = {
                    RecipeEntry._ID,
                    RecipeEntry.COLUMN_RECIPE_SOURCE_ID,
                    RecipeEntry.COLUMN_NAME,
                    RecipeEntry.COLUMN_SERVINGS,
                    RecipeEntry.COLUMN_IMG_URL,
                    RecipeEntry.COLUMN_IS_FAVORITED
            };

            // Check if the recipe exists in the database
            Cursor cursor = getContext().getContentResolver().query(
                    RecipeEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()){
                recipes = new Recipe[cursor.getCount()];

                String name, img_url;
                int recipeId, sourceId, servings;
                boolean isFavorite;

                recipeId = cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID));
                sourceId = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SOURCE_ID));
                name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
                servings = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_SERVINGS));
                img_url = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_IMG_URL));

                List<Ingredient> ingredients = getIngredients(recipeId);
                List<RecipeStep> steps = getSteps(recipeId);

                Recipe recipe = new Recipe(
                        sourceId, name, ingredients, steps, servings, img_url
                        );
                recipes[0] = recipe;

                for (int i = 1; cursor.moveToNext(); i++) {

                    recipeId = cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID));
                    sourceId = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SOURCE_ID));
                    name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
                    servings = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_SERVINGS));
                    img_url = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_IMG_URL));
                    //isFavorite = (cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_IS_FAVORITED)) == 1);

                    ingredients = getIngredients(recipeId);
                    steps = getSteps(recipeId);

                    recipe = new Recipe(
                            sourceId, name, ingredients, steps, servings, img_url
                    );
                    recipes[i] = recipe;
                }
            }

            cursor.close();
            return recipes;
        }

        return QueryUtils.fetchRecipes(mUrl);
    }

    private ArrayList<Ingredient> getIngredients(int recipeId){

        ArrayList<Ingredient> ingredientsList = new ArrayList<>();

        // Get the Ingredient information from the database
        String[] ingredientProjection = {
                IngredientEntry._ID,
                IngredientEntry.COLUMN_RECIPE_ID,
                IngredientEntry.COLUMN_QUANTITY,
                IngredientEntry.COLUMN_MEASUREMENT,
                IngredientEntry.COLUMN_NAME
        };

        String selection = IngredientEntry.COLUMN_RECIPE_ID + "=?";

        String[] selectionArgs = {Integer.toString(recipeId)};

        // Check if the Ingredient exists in the database
        Cursor ingredientCursor = getContext().getContentResolver().query(
                IngredientEntry.CONTENT_URI,
                ingredientProjection,
                selection,
                selectionArgs,
                null,
                null
        );

        if (ingredientCursor != null){

            if (ingredientCursor.moveToFirst()){

                long quantity;
                String name, measurement;

                quantity = ingredientCursor.getInt(ingredientCursor.getColumnIndex(IngredientEntry.COLUMN_QUANTITY));
                measurement = ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientEntry.COLUMN_MEASUREMENT));
                name = ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientEntry.COLUMN_NAME));

                Ingredient ingredient = new Ingredient(quantity, measurement, name);
                ingredientsList.add(ingredient);

                while (ingredientCursor.moveToNext()) {

                    quantity = ingredientCursor.getLong(ingredientCursor.getColumnIndex(IngredientEntry.COLUMN_QUANTITY));
                    measurement = ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientEntry.COLUMN_MEASUREMENT));
                    name = ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientEntry.COLUMN_NAME));

                    ingredient = new Ingredient(quantity, measurement, name);
                    ingredientsList.add(ingredient);
                }
            }

            ingredientCursor.close();
        }

        return ingredientsList;
    }

    private ArrayList<RecipeStep> getSteps(int recipeId){

        ArrayList<RecipeStep> recipeStepsList = new ArrayList<>();

        // Get the Ingredient information from the database
        String[] stepProjection = {
                RecipeStepEntry._ID,
                RecipeStepEntry.COLUMN_RECIPE_ID,
                RecipeStepEntry.COLUMN_RECIPE_STEP_ID,
                RecipeStepEntry.COLUMN_SHORT_DESCRIPTION,
                RecipeStepEntry.COLUMN_DESCRIPTION,
                RecipeStepEntry.COLUMN_STEP_IMG_URL,
                RecipeStepEntry.COLUMN_STEP_VIDEO_URL
        };

        String selection = RecipeStepEntry.COLUMN_RECIPE_ID + "=?";

        String[] selectionArgs = {Integer.toString(recipeId)};

        // Check if the Ingredient exists in the database
        Cursor stepCursor = getContext().getContentResolver().query(
                RecipeStepEntry.CONTENT_URI,
                stepProjection,
                selection,
                selectionArgs,
                null,
                null
        );

        if (stepCursor != null){

            if (stepCursor.moveToFirst()){

                int recipeStepId;
                String shortDescription, description, imageUrl, videoUrl;

                recipeId = stepCursor.getInt(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_RECIPE_STEP_ID));
                shortDescription = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_SHORT_DESCRIPTION));
                description = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_DESCRIPTION));
                videoUrl = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_STEP_VIDEO_URL));
                imageUrl = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_STEP_IMG_URL));

                RecipeStep step = new RecipeStep(recipeId, shortDescription, description, videoUrl, imageUrl);
                recipeStepsList.add(step);

                while (stepCursor.moveToNext()) {

                    recipeId = stepCursor.getInt(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_RECIPE_STEP_ID));
                    shortDescription = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_SHORT_DESCRIPTION));
                    description = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_DESCRIPTION));
                    videoUrl = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_STEP_VIDEO_URL));
                    imageUrl = stepCursor.getString(stepCursor.getColumnIndex(RecipeStepEntry.COLUMN_STEP_IMG_URL));

                    step = new RecipeStep(recipeId, shortDescription, description, videoUrl, imageUrl);
                    recipeStepsList.add(step);
                }
            }

            stepCursor.close();
        }

        return recipeStepsList;
    }
}
