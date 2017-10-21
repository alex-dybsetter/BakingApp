package net.alexblass.bakingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import net.alexblass.bakingapp.models.Group;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;
import net.alexblass.bakingapp.utilities.ExpandableRecipeAdapter;
import net.alexblass.bakingapp.utilities.RecipeQueryUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;
import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_KEY;

/**
 * The activity that displays the detailed information about a Recipe.
 */

public class RecipeOverviewActivity<T> extends AppCompatActivity {

    // The Recipe that we're viewing
    private Recipe mSelectedRecipe;

    // The selected RecipeStep
    RecipeStep mSelectedStep;

    // An expandable recycler view adapter to display the detail data of a recipe,
    // such as the list of ingredients and the list of steps.
    private ExpandableRecipeAdapter mAdapter;

    // The RecyclerView to display all the Ingredients and RecipeSteps
    private RecyclerView mRecyclerView;

    // A list of groups for ingredients and steps
    private List<Group> mGroups;

    // Determines if the app is running in two-pane view on a Tablet
    private boolean mTwoPane;

    // The fragment created by a click on the recycler view
    private RecipeStepDetailFragment mStepDetailFragment;

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

                mRecyclerView = (RecyclerView) findViewById(R.id.expandableList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);

                mGroups = new ArrayList<>();
                mGroups.add(new Group(getString(R.string.ingredients_title), mSelectedRecipe.getIngredients()));
                mGroups.add(new Group(getString(R.string.steps_title), mSelectedRecipe.getSteps()));

                mAdapter = new ExpandableRecipeAdapter(this, mGroups);

                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mAdapter);

                for (int i = mAdapter.getGroups().size() - 1; i >= 0; i--) {
                    expandGroup(i);
                }

                if (findViewById(R.id.recipe_step_container) != null) {
                    mTwoPane = true;
                }

                mAdapter.setChildClickListener(new ExpandableRecipeAdapter.ItemClickListener() {
                    @Override
                    public void onChildItemClick(View view, int position) {
                        mStepDetailFragment = RecipeStepDetailFragment.newInstance(mSelectedRecipe, mAdapter.getStep());

                        if (mTwoPane) {
                            // Make sure the ActionBar is showing and has the title of the Recipe
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            getSupportActionBar().show();
                            getSupportActionBar().setTitle(mSelectedRecipe.getName());

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.recipe_step_container, mStepDetailFragment)
                                    .commit();
                        } else {

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, mStepDetailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
            }
        }
    }

    // Expand all the groups on launch activity
    public void expandGroup (int gPos){
        if(mAdapter.isGroupExpanded(gPos)){
            return;
        }
        mAdapter.toggleGroup(gPos);
    }

    private void delete(){
        if (mSelectedRecipe.getId() != -1){
            Toast.makeText(getApplicationContext(), getString(R.string.cannot_delete), Toast.LENGTH_SHORT).show();
        }else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            RecipeQueryUtils.delete(getApplicationContext(), mSelectedRecipe.getDbId());
                            Toast.makeText(getApplicationContext(), getString(R.string.deleted_confirmation), Toast.LENGTH_SHORT).show();
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // Do nothing if the user clicks no
                            break;
                    }
                }
            };

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.delete_prompt))
                    .setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener);

            dialog.create().show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.action_favorite);
        checkable.setChecked(mSelectedRecipe.getIsFavorite());
        return true;
    }

    // Inflate our options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_overview, menu);
        return true;
    }

    // Determine what action to take based on the menu item selected
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
            case R.id.action_delete_entry:
                delete();
                return true;
            case R.id.action_favorite:
                mSelectedRecipe.setIsFavorite(!mSelectedRecipe.getIsFavorite());
                RecipeQueryUtils.updateFavorite(this,
                        mSelectedRecipe.getDbId(), mSelectedRecipe.getIsFavorite());
                item.setChecked(mSelectedRecipe.getIsFavorite());
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }
}