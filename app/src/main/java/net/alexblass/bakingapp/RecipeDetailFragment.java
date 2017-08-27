package net.alexblass.bakingapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.IngredientAdapter;

import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * This Fragment allows users to view the detailed information about a Recipe.
 */

public class RecipeDetailFragment extends Fragment {

    // The Recipe that we're viewing
    private Recipe mSelectedRecipe;

    // A RecyclerView to display all the Ingredients
    private RecyclerView mIngredientsRecyclerView;

    // A RecyclerView to display all the RecipeSteps
    private RecyclerView mStepsRecyclerView;

    // The LinearLayoutManager to display Ingredients and RecipeSteps in a list
    private LinearLayoutManager mLayoutManager;

    // An IngredientAdapter to display the Ingredients correctly
    private IngredientAdapter mIngredientAdapter;

    // Empty constructor
    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity != null) {
            // If there's a valid Recipe, get the data from the Recipe and display it
            if (intentThatStartedThisActivity.hasExtra(RECIPE_KEY)) {
                mSelectedRecipe = intentThatStartedThisActivity.getParcelableExtra(RECIPE_KEY);

                // Set the action bar title to the Recipe name
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mSelectedRecipe.getName());

                mIngredientsRecyclerView = (RecyclerView) rootView.findViewById(R.id.ingredient_rv);
                mStepsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_step_rv);

                mLayoutManager = new LinearLayoutManager(getActivity());
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                mIngredientsRecyclerView.setLayoutManager(mLayoutManager);
                mIngredientsRecyclerView.setHasFixedSize(true);
//                mStepsRecyclerView.setLayoutManager(mLayoutManager);
//                mStepsRecyclerView.setHasFixedSize(true);

                mIngredientAdapter =
                        new IngredientAdapter(getActivity(), mSelectedRecipe.getIngredients());
                mIngredientsRecyclerView.setAdapter(mIngredientAdapter);
            }
        }

        return rootView;
    }
}
