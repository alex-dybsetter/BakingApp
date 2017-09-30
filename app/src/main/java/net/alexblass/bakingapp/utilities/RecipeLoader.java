package net.alexblass.bakingapp.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import net.alexblass.bakingapp.data.RecipeQueryUtils;
import net.alexblass.bakingapp.models.Recipe;

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
        if (mUrl == null){
            RecipeQueryUtils utils = new RecipeQueryUtils(getContext());
            return utils.getRecipes();
        }

        return QueryUtils.fetchRecipes(mUrl);
    }
}
