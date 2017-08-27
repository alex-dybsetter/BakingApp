package net.alexblass.bakingapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * This Fragment allows users to view the detailed information about a Recipe.
 */

public class RecipeDetailFragment<T> extends Fragment {

    // The Recipe that we're viewing
    private Recipe mSelectedRecipe;

    // An expandable list adapter to display the detail data of a recipe,
    // such as the list of ingredients and the list of steps.
    private ExpandableListAdapter mAdapter;

    // The ExpandableListView to display all the Ingredients and RecipeSteps
    private ExpandableListView mExpListView;

    // A list to hold the titles of each section
    private List<String> mSectionTitleList;

    // A list to hold the section children data
    private HashMap<String, List<T>> mSectionChildList;

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

                mExpListView = (ExpandableListView) rootView.findViewById(R.id.expandableList);

                mSectionTitleList = new ArrayList<>();
                mSectionChildList = new HashMap<>();

                mSectionTitleList.add(getActivity().getString(R.string.ingredients_title));
                mSectionTitleList.add(getActivity().getString(R.string.steps_title));

                mSectionChildList.put(mSectionTitleList.get(0), (List<T>) mSelectedRecipe.getIngredients());
                mSectionChildList.put(mSectionTitleList.get(1), (List<T>) mSelectedRecipe.getSteps());

                mAdapter = new ExpandableListAdapter(getActivity(), mSectionTitleList, mSectionChildList);

                mExpListView.setAdapter(mAdapter);
            }
        }
        return rootView;
    }
}
