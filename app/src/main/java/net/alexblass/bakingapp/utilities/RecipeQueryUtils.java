package net.alexblass.bakingapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.alexblass.bakingapp.data.IngredientsContract.IngredientEntry;
import net.alexblass.bakingapp.data.RecipesContract.RecipeEntry;
import net.alexblass.bakingapp.data.RecipeStepsContract.RecipeStepEntry;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to get complete Recipes from the 3 databases.
 */

public class RecipeQueryUtils {

    private Context mContext;

    // A constructor to get the context so we can access the provider
    public RecipeQueryUtils(Context context){ this.mContext = context; }

    // Get all the Recipes in an array
    public Recipe[] getRecipes(){
        Recipe[] recipes = null;

        String[] projection = {
                RecipeEntry._ID
        };

        // Check if the recipe exists in the database
        Cursor cursor = mContext.getContentResolver().query(
                RecipeEntry.CONTENT_URI,
                projection,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                recipes = new Recipe[cursor.getCount()];

                int recipeId;

                recipeId = cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID));
                recipes[0] = getRecipe(recipeId);

                for (int i = 1; cursor.moveToNext(); i++) {
                    recipeId = cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID));

                    recipes[i] = getRecipe(recipeId);
                }
            }
            cursor.close();
        }
        return recipes;
    }

    // Get the ArrayList of Ingredients for a given Recipe
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
        Cursor ingredientCursor = mContext.getContentResolver().query(
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

    // Get the ArrayList of RecipeSteps for a given Recipe
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
        Cursor stepCursor = mContext.getContentResolver().query(
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

    // Get a single Recipe
    public Recipe getRecipe(int recipeId){
        Recipe recipe = null;

        String[] projection = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_RECIPE_SOURCE_ID,
                RecipeEntry.COLUMN_NAME,
                RecipeEntry.COLUMN_SERVINGS,
                RecipeEntry.COLUMN_IMG_URL,
                RecipeEntry.COLUMN_IS_FAVORITED
        };

        String selection = RecipeEntry._ID + "=?";

        String[] selectionArgs = {Integer.toString(recipeId)};

        // Check if the recipe exists in the database
        Cursor cursor = mContext.getContentResolver().query(
                RecipeEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {

            String name, img_url;
            int sourceId, servings, dbId;
            boolean isFavorite;

            dbId = cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID));
            sourceId = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SOURCE_ID));
            name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
            servings = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_SERVINGS));
            img_url = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_IMG_URL));
            isFavorite = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_IS_FAVORITED)) == 1;

            List<Ingredient> ingredients = getIngredients(recipeId);
            List<RecipeStep> steps = getSteps(recipeId);

            recipe = new Recipe(
                    sourceId, name, ingredients, steps, servings, img_url, isFavorite, dbId
            );

            cursor.close();
        }

        return recipe;
    }

    // Update the database value for whether a recipe is favorited or not
    public boolean updateFavorite(int recipeId, boolean favorite){

        if (recipeId == -1){
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_IS_FAVORITED, favorite);

        String selection = RecipeEntry._ID + "=?";

        String[] selectionArgs = {Integer.toString(recipeId)};

        // Update the values
        int rowsUpdated = mContext.getContentResolver().update(
                RecipeEntry.CONTENT_URI,
                values,
                selection,
                selectionArgs
        );

        return rowsUpdated > 0;
    }
}