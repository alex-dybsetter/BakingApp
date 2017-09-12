package net.alexblass.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;
import net.alexblass.bakingapp.utilities.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;
import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * The activity that displays the detailed information about a Recipe.
 */

public class RecipeOverviewActivity<T> extends AppCompatActivity {

    // The key to pass and get RecipeSteps from Intents
    public static final String RECIPE_STEP_KEY = "recipe_step";

    // The Recipe that we're viewing
    private Recipe mSelectedRecipe;

    // The selected RecipeStep
    RecipeStep mSelectedStep;

    // An expandable list adapter to display the detail data of a recipe,
    // such as the list of ingredients and the list of steps.
    private ExpandableListAdapter mAdapter;

    // The ExpandableListView to display all the Ingredients and RecipeSteps
    private ExpandableListView mExpListView;

    // A list to hold the titles of each section
    private List<String> mSectionTitleList;

    // A list to hold the section children data
    private HashMap<String, List<T>> mSectionChildList;

    // Determines if the app is running in two-pane view on a Tablet
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_overview);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            // If there's a valid Recipe, get the data from the Recipe and display it
            if (intentThatStartedThisActivity.hasExtra(RECIPE_KEY)) {
                mSelectedRecipe = intentThatStartedThisActivity.getParcelableExtra(RECIPE_KEY);

                // Make sure the ActionBar is showing and has the title of the Recipe
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getSupportActionBar().show();
                getSupportActionBar().setTitle(mSelectedRecipe.getName());

                mExpListView = (ExpandableListView) findViewById(R.id.expandableList);

                mSectionTitleList = new ArrayList<>();
                mSectionChildList = new HashMap<>();

                mSectionTitleList.add(getString(R.string.ingredients_title));
                mSectionTitleList.add(getString(R.string.steps_title));

                mSectionChildList.put(mSectionTitleList.get(0), (List<T>) mSelectedRecipe.getIngredients());
                mSectionChildList.put(mSectionTitleList.get(1), (List<T>) mSelectedRecipe.getSteps());

                mAdapter = new ExpandableListAdapter(this, mSectionTitleList, mSectionChildList);

                mExpListView.setAdapter(mAdapter);

                for (int i = 0; i < mAdapter.getGroupCount(); i++) {
                    mExpListView.expandGroup(i);
                }

                mExpListView
                        .setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                            @Override
                            public boolean onChildClick(
                                    ExpandableListView parent, View v,
                                    int groupPosition, int childPosition,
                                    long id) {

                                mSelectedStep = (RecipeStep)
                                        mAdapter.getChild(groupPosition, childPosition);

                                if (mTwoPane) {
                                    // Make sure the ActionBar is showing and has the title of the Recipe
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                    getSupportActionBar().show();
                                    getSupportActionBar().setTitle(mSelectedRecipe.getName());

                                    Bundle args = new Bundle();
                                    args.putParcelable(RECIPE_STEP_KEY, mSelectedStep);
                                    args.putParcelable(RECIPE_KEY, mSelectedRecipe);

                                    RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
                                    stepDetailFragment.setArguments(args);

                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.recipe_step_container, stepDetailFragment)
                                            .commit();
                                } else {
                                    Bundle args = new Bundle();
                                    args.putParcelable(RECIPE_STEP_KEY, mSelectedStep);
                                    args.putParcelable(RECIPE_KEY, mSelectedRecipe);

                                    RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
                                    stepDetailFragment.setArguments(args);

                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, stepDetailFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                                return false;
                            }
                        });
            }
        }

        if (findViewById(R.id.recipe_step_container) != null) {
            mTwoPane = true;
        }
    }

    // Override method to determine action on Up button pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // On Up button click, return to previous Fragment or MainActivity
            case android.R.id.home:
                // getBackStackEntryCount must be greater than 1 so that we don't end up with
                // an empty Fragment screen.
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    // Relaunch this activity so we're back at the Overview page
                    Intent intentThatStartedThisActivity = getIntent();
                    startActivity(intentThatStartedThisActivity);
                } else {
                    navigateUpFromSameTask(this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}