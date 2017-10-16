package net.alexblass.bakingapp.utilities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

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

    // A private empty constructor so no RecipeQueryUtils object is created
    private RecipeQueryUtils(){}

    // Updates the table so that we have the most recent recipe data without adding duplicates
    public static void updateTable(Context context, Recipe[] recipes){
        for (Recipe recipe: recipes) {
            // Get the Recipe by its source id since any user-added recipes will not have one
            String sourceId = Integer.toString(recipe.getId());

            String[] projection = {
                    RecipeEntry.COLUMN_RECIPE_SOURCE_ID
            };

            String selection = RecipeEntry.COLUMN_RECIPE_SOURCE_ID + "=?";

            String[] selectionArgs = {sourceId};

            Cursor cursor = context.getContentResolver().query(
                    RecipeEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null
            );

            // Only add the recipe to the database if it does not already exist
            if (cursor != null) {
                long recipeId = -1;
                if (!cursor.moveToFirst()) {

                    recipeId = addRecipe(context, recipe, false);

                    // Add the Recipe Ingredients
                    for (Ingredient i : recipe.getIngredients()) {
                        addIngredient(context, i, recipeId);
                    }

                    // Add the RecipeSteps
                    for (RecipeStep step : recipe.getSteps()) {
                        addStep(context, step, recipeId);
                    }
                }
                cursor.close();
            }
        }
    }

    // Get all the Recipes in an array
    public static Recipe[] getRecipes(Context context){
        Recipe[] recipes = null;

        String[] projection = {
                RecipeEntry._ID
        };

        // Check if the recipe exists in the database
        Cursor cursor = context.getContentResolver().query(
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
                recipes[0] = getRecipe(context, recipeId);

                for (int i = 1; cursor.moveToNext(); i++) {
                    recipeId = cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID));

                    recipes[i] = getRecipe(context, recipeId);
                }
            }
            cursor.close();
        }
        return recipes;
    }

    // Get the ArrayList of Ingredients for a given Recipe
    private static ArrayList<Ingredient> getIngredients(Context context, int recipeId){

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
        Cursor ingredientCursor = context.getContentResolver().query(
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
    private static ArrayList<RecipeStep> getSteps(Context context, int recipeId){

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
        Cursor stepCursor = context.getContentResolver().query(
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
    public static Recipe getRecipe(Context context, int recipeId){
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
        Cursor cursor = context.getContentResolver().query(
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

            List<Ingredient> ingredients = getIngredients(context, recipeId);
            List<RecipeStep> steps = getSteps(context, recipeId);

            recipe = new Recipe(
                    sourceId, name, ingredients, steps, servings, img_url, isFavorite, dbId
            );

            cursor.close();
        }

        return recipe;
    }

    // Add a recipe to the database
    public static long addRecipe(Context context, Recipe recipe, boolean isFave){
        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_RECIPE_SOURCE_ID, recipe.getId());
        values.put(RecipeEntry.COLUMN_NAME, recipe.getName());
        values.put(RecipeEntry.COLUMN_SERVINGS, recipe.getServings());
        values.put(RecipeEntry.COLUMN_IMG_URL, recipe.getImageUrl());
        values.put(RecipeEntry.COLUMN_IS_FAVORITED, isFave);

        Uri newUri = context.getContentResolver().insert(RecipeEntry.CONTENT_URI, values);

        // Return the Recipe ID
        return ContentUris.parseId(newUri);
    }

    // Delete a recipe from the database
    public static int delete(Context context, int recipeId){
        if (recipeId == -1){
            return -1;
        }

        String selection = RecipeEntry._ID + "=?";

        String[] selectionArgs = {Integer.toString(recipeId)};

        return context.getContentResolver().delete(
                Uri.parse(RecipeEntry.CONTENT_URI + "/" + recipeId),
                selection,
                selectionArgs
        );
    }

    // Add a ingredient to the database
    public static long addIngredient(Context context, Ingredient ingredient, long recipeId){
        ContentValues values = new ContentValues();
        values.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
        values.put(IngredientEntry.COLUMN_QUANTITY, ingredient.getQuantity());
        values.put(IngredientEntry.COLUMN_MEASUREMENT, ingredient.getMeasurement());
        values.put(IngredientEntry.COLUMN_NAME, ingredient.getIngredientName());

        Uri newUri = context.getContentResolver().insert(IngredientEntry.CONTENT_URI, values);

        // Return the Ingredient ID
        return ContentUris.parseId(newUri);
    }

    // Add a ingredient to the database
    public static long addStep(Context context, RecipeStep step, long recipeId){
        ContentValues values = new ContentValues();
        values.put(RecipeStepEntry.COLUMN_RECIPE_ID, recipeId);
        values.put(RecipeStepEntry.COLUMN_RECIPE_STEP_ID, step.getId());
        values.put(RecipeStepEntry.COLUMN_SHORT_DESCRIPTION, step.getShortDescription());
        values.put(RecipeStepEntry.COLUMN_DESCRIPTION, step.getDescription());
        values.put(RecipeStepEntry.COLUMN_STEP_IMG_URL, step.getImageUrl());
        values.put(RecipeStepEntry.COLUMN_STEP_VIDEO_URL, step.getVideoUrl());

        Uri newUri = context.getContentResolver().insert(RecipeStepEntry.CONTENT_URI, values);
        // Return the Step ID
        return ContentUris.parseId(newUri);
    }

    // Update the database value for whether a recipe is favorited or not
    public static boolean updateFavorite(Context context, int recipeId, boolean favorite){

        if (recipeId == -1){
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_IS_FAVORITED, favorite);

        String selection = RecipeEntry._ID + "=?";

        String[] selectionArgs = {Integer.toString(recipeId)};

        // Update the values
        int rowsUpdated = context.getContentResolver().update(
                RecipeEntry.CONTENT_URI,
                values,
                selection,
                selectionArgs
        );

        return rowsUpdated > 0;
    }
}