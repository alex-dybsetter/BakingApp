package net.alexblass.bakingapp.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;

import net.alexblass.bakingapp.models.Recipe;

/**
 * Loads a list of Recipes using the AsyncTask
 * to perform the network request to the JSON URL.
 */

public class RecipeLoader extends AsyncTaskLoader<Recipe[]> {
    // The query URL
    private String mUrl =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    // Constructs a new RecipeLoader
    public RecipeLoader(Context context){
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Recipe[] loadInBackground() {
        return QueryUtils.fetchRecipes(mUrl);
    }
}
