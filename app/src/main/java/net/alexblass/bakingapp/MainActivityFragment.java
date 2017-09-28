package net.alexblass.bakingapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.bakingapp.data.RecipesContract.RecipeEntry;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.RecipeAdapter;
import net.alexblass.bakingapp.utilities.RecipeLoader;

/**
 * This fragment allows users to view a list of recipes
 * in a RecyclerView that are pulled from a JSON file.
 */

public class MainActivityFragment extends Fragment
        implements RecipeAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Recipe[]>{
    // The key to pass and get Recipes from Intents
    public static final String RECIPE_KEY = "recipe";

    // The ID for the recipe loader
    private static final int RECIPE_LOADER_ID = 0;
    // The ID for the recipe favorites loader
    private static final int FAVORITE_RECIPE_LOADER_ID = 1;

    // The query URL
    private String mUrl;

    // Displays a message when there is no Internet or when there are no Recipes found
    private TextView mErrorMessageTextView;
    // The error message to display
    private String mErrorMessage;

    // Loading indicator for a responsive app experience
    private View mLoadingIndicator;

    // A RecyclerView to display all the Recipe cards
    private RecyclerView mRecyclerView;
    // The LinearLayoutManager to display recipes in a list
    private LinearLayoutManager mLayoutManager;

    // A RecipeAdapter to display the Recipes correctly
    private RecipeAdapter mAdapter;

    // Saved position of the recycler view
    private int mPosition = RecyclerView.NO_POSITION;

    // Empty constructor
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mUrl = getActivity().getResources().getString(R.string.query_url);

        // Find the RecyclerView and set our adapter to it so the recipes
        // display in a vertical linear layout format.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipes_rv);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecipeAdapter(getActivity(), new Recipe[0]);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        // Set the LoadingIndicator to visible as the data loads
        mLoadingIndicator = rootView.findViewById(R.id.loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        mErrorMessageTextView = (TextView) rootView.findViewById(R.id.error_message_tv);

        LoaderManager loaderManager = getLoaderManager();

        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        // If there is no connectivity, use our local SQLite database to show recipes
        if (isConnected) {
            loaderManager.initLoader(RECIPE_LOADER_ID, null, this);
        } else {
            loaderManager.initLoader(FAVORITE_RECIPE_LOADER_ID, null, this);
        }
        showRecyclerView();

        return rootView;
    }

    // Set the data view to visible and the error message view to invisible
    private void showRecyclerView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // Set the data view to invisible and the error message to visible
    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    // Add a recipe to the database
    private void addRecipe(Recipe recipe, boolean isFave){
        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_RECIPE_SOURCE_ID, recipe.getId());
        values.put(RecipeEntry.COLUMN_NAME, recipe.getName());
        values.put(RecipeEntry.COLUMN_SERVINGS, recipe.getServings());
        values.put(RecipeEntry.COLUMN_IMG_URL, recipe.getImageUrl());
        values.put(RecipeEntry.COLUMN_IS_FAVORITED, isFave);

        Uri newUri = getActivity().getContentResolver().insert(RecipeEntry.CONTENT_URI, values);
    }

    // Updates the table so that we have the most recent recipe data without adding duplicates
    private void updateTable(Recipe recipe, boolean isFave){
        // Get the Recipe by its source id since any user-added recipes will not have one
        String sourceId = Integer.toString(recipe.getId());

        String[] projection = {
                RecipeEntry.COLUMN_RECIPE_SOURCE_ID
        };

        String selection = RecipeEntry.COLUMN_RECIPE_SOURCE_ID + "=?";

        String[] selectionArgs = {sourceId};

        Cursor cursor = getActivity().getContentResolver().query(
                RecipeEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        // Only add the recipe to the database if it does not already exist
        if (!cursor.moveToFirst()){
            addRecipe(recipe, isFave);
        }

        cursor.close();
    }

    // Load the data from the JSON file URL
    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        RecipeLoader recipeLoader;
        mErrorMessage = getString(R.string.no_results);
        switch(id) {
            case RECIPE_LOADER_ID:
                Uri baseUri = Uri.parse(mUrl);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                recipeLoader = new RecipeLoader(getActivity(), uriBuilder.toString());
                return recipeLoader;
            case FAVORITE_RECIPE_LOADER_ID:
                // Since we're pulling our data from the database, we do not need a URL
                recipeLoader = new RecipeLoader(getActivity(), null);
                // If there are no favorites, show an error message
                if(recipeLoader.loadInBackground() == null){
                    mLoadingIndicator.setVisibility(View.GONE);
                    mErrorMessage = getString(R.string.no_results);
                    showErrorMessage();
                }
                return recipeLoader;
            default:
                return null;
        }
    }

    // When the Loader finishes loading, add the list of Recipes to the Adapter data set
    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] newRecipes) {
        mLoadingIndicator.setVisibility(View.GONE);
        mErrorMessageTextView.setText(mErrorMessage);
        mAdapter.setAllRecipies(new Recipe[0]);

        if (newRecipes != null && newRecipes.length > 0){
            mAdapter.setAllRecipies(newRecipes);

            for (Recipe r : newRecipes) {
                updateTable(r, false);
            }
        } else {
            showErrorMessage();
        }

        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;}
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    // Reset the loader to clear existing data
    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {
        mAdapter.setAllRecipies(new Recipe[0]);
    }

    // Respond to click events by launching the detail activity
    @Override
    public void onItemClick(View view, int position) {
        Intent detailIntent = new Intent(getActivity(), RecipeOverviewActivity.class);

        Recipe selectedRecipe = mAdapter.getItem(position);
        detailIntent.putExtra(RECIPE_KEY, selectedRecipe);

        startActivity(detailIntent);
    }
}
