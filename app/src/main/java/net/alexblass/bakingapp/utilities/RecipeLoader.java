package net.alexblass.bakingapp.utilities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Recipe;


/**
 * Loads a list of Recipes using the AsyncTask
 * to perform the network request to the JSON URL.
 */

public class RecipeLoader extends AsyncTaskLoader<Recipe[]> {
    // The query URL
    private String mUrl = null;

    // Constructs a new RecipeLoader
    public RecipeLoader(Context context){
        super(context);

        // If we are connected to the internet, then we can add the API URL
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            mUrl = context.getResources().getString(R.string.query_url);
            Uri baseUri = Uri.parse(mUrl);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            mUrl = uriBuilder.toString();
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Recipe[] loadInBackground() {
        if (mUrl != null) {
            // When connected to the internet, update the database but load from our content
            // provider anyway so that the data displayed shows the locally created recipes too
            QueryUtils.fetchRecipes(mUrl);
        }

        RecipeQueryUtils utils = new RecipeQueryUtils(getContext());
        return utils.getRecipes();
    }
}
