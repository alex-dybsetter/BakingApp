package net.alexblass.bakingapp.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import net.alexblass.bakingapp.data.RecipesContract.RecipeEntry;
import net.alexblass.bakingapp.models.Recipe;


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
                int sourceId, servings;
                boolean isFavorite;

                sourceId = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SOURCE_ID));
                name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
                servings = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_SERVINGS));
                img_url = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_IMG_URL));

                Recipe recipe = new Recipe(
                        sourceId, name, null, null, servings, img_url
                        );
                recipes[0] = recipe;

                for (int i = 1; cursor.moveToNext(); i++) {

                    sourceId = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SOURCE_ID));
                    name = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_NAME));
                    servings = cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_SERVINGS));
                    img_url = cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_IMG_URL));
                    //isFavorite = (cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_IS_FAVORITED)) == 1);

                    recipe = new Recipe(
                            sourceId, name, null, null, servings, img_url
                    );
                    recipes[i] = recipe;
                }
            }

            cursor.close();
            return recipes;
        }

        return QueryUtils.fetchRecipes(mUrl);
    }
}
