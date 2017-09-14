package net.alexblass.bakingapp.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Recipe;

/**
 * Loads a list of Recipes using the AsyncTask
 * to perform the network request to the JSON URL.
 */

public class RecipeLoader extends AsyncTaskLoader<Recipe[]> {
    // The query URL
    private String mUrl = getContext().getResources().getString(R.string.query_url);

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
