package net.alexblass.bakingapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.RecipeAdapter;

/**
 * This fragment allows users to view a list of recipes
 * in a RecyclerView that are pulled from a JSON file.
 */

public class MainActivityFragment extends Fragment
        implements RecipeAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Recipe[]>{
    // The ID for the recipe loader
    private static final int RECIPE_LOADER_ID = 0;

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

    // Empty constructor
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Find the RecyclerView and set our adapter to it so the recipes
        // display in a vertical linear layout format.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipes_rv);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        Recipe[] testList = new Recipe[5];
        testList[0] = new Recipe("Fried green tomatoes", 5);
        testList[1] = new Recipe("Ravioli", 4);
        testList[2] = new Recipe("Chocolate cake", 8);
        testList[3] = new Recipe("Empanadas", 16);
        testList[4] = new Recipe("Apple pie", 6);

        mAdapter = new RecipeAdapter(getActivity(), testList);
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

        // If there is no connectivity, show an error message
        if (isConnected) {
            showRecyclerView();
            loaderManager.initLoader(RECIPE_LOADER_ID, null, this);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            showErrorMessage();
            mErrorMessageTextView.setText(R.string.no_connection);
        }

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

    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.GONE);
        return null;
        // TODO: Get the data from the JSON file url
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] data) {
        mLoadingIndicator.setVisibility(View.GONE);
        //TODO: Update the adapter with the data
    }

    // Reset the loader to clear existing data
    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {
        mAdapter.setAllRecipies(new Recipe[0]);
    }

    // Respond to click events by launching the detail activity
    @Override
    public void onItemClick(View view, int position) {
        // TODO: Launch detail activity
    }
}
