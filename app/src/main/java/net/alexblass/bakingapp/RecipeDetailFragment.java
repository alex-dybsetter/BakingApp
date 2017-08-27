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
import net.alexblass.bakingapp.utilities.RecipeStepAdapter;

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

    // An IngredientAdapter to display the Ingredients correctly
    private IngredientAdapter mIngredientAdapter;

    // A RecipeStepAdapter to display the RecipeSteps correctly
    private RecipeStepAdapter mStepAdapter;

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

                mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(
                        getActivity()));
                mIngredientsRecyclerView.setHasFixedSize(true);

                mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(
                        getActivity()));
                mStepsRecyclerView.setHasFixedSize(true);

                mIngredientAdapter =
                        new IngredientAdapter(getActivity(), mSelectedRecipe.getIngredients());
                mIngredientsRecyclerView.setAdapter(mIngredientAdapter);

                mStepAdapter =
                        new RecipeStepAdapter(getActivity(), mSelectedRecipe.getSteps());
                mStepsRecyclerView.setAdapter(mStepAdapter);
            }
        }

        return rootView;
    }
}
