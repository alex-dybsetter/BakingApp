package net.alexblass.bakingapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.RecipeAdapter;
import net.alexblass.bakingapp.utilities.RecipeLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_KEY;

/**
 * This fragment allows users to view a list of recipes
 * in a RecyclerView that are pulled from a JSON file.
 */

public class MainActivityFragment extends Fragment
        implements RecipeAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<Recipe[]>{

    // The number of columns in the grid recyclerview on tablets
    private static final int NUMBER_OF_COLS = 3;

    // The ID for the recipe loader
    private static final int RECIPE_LOADER_ID = 0;

    // Displays a message when there is no Internet or when there are no Recipes found
    @BindView(R.id.error_message_tv) TextView mErrorMessageTextView;
    // The error message to display
    private String mErrorMessage;

    // Loading indicator for a responsive app experience
    @BindView(R.id.loading_indicator) View mLoadingIndicator;

    // A RecyclerView to display all the Recipe cards
    @BindView(R.id.recipes_rv) RecyclerView mRecyclerView;
    // The LinearLayoutManager to display recipes in a list on phones
    private LinearLayoutManager mLinearLayoutManager;
    // The GridLayoutManager to display recipes in a grid on tablets
    private GridLayoutManager mGridLayoutManager;

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
        ButterKnife.bind(this, rootView);

        // Find the RecyclerView and set our adapter to it to display the recipes
        if (getResources().getBoolean(R.bool.isTablet)){
            mGridLayoutManager = new GridLayoutManager(getActivity(), NUMBER_OF_COLS);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
        } else {
            mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        }
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecipeAdapter(getActivity(), new Recipe[0]);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        // Set the LoadingIndicator to visible as the data loads
        mLoadingIndicator.setVisibility(View.VISIBLE);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(RECIPE_LOADER_ID, null, this);
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

    // Load the data from the JSON file URL
    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        RecipeLoader recipeLoader;
        mErrorMessage = getString(R.string.no_results);
        switch(id) {
            case RECIPE_LOADER_ID:
                recipeLoader = new RecipeLoader(getActivity());
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
        } else {
            // If there are no recipes, show an error message
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
