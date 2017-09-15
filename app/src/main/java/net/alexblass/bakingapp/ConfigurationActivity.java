package net.alexblass.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.QueryUtils;
import net.alexblass.bakingapp.utilities.WidgetService;

import java.util.ArrayList;
import java.util.List;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * An Activity that allows users to configure their widget settings to choose a Recipe.
 */

public class ConfigurationActivity extends Activity {

    public static final String PREFS_KEY = "prefs";

    // An Array of Recipes pulled from the network JSON url
    private Recipe[] mRecipesArray;
    // A list of the Recipe names
    private List<String> mRecipesNamesList;
    // The spinner of Recipe options
    private Spinner mRecipeOptions;
    // Selected Recipe
    private Recipe mSelectedRecipe;
    // The widget ID
    private int mAppWidgetId;
    // The shared preferences to store our recipe
    SharedPreferences mPrefs;

    // TODO: Show preferences if already saved
    // TODO: Update preferences if already saved and now overwritten

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        setResult(RESULT_CANCELED);

        mPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        initListViews();
    }

    public void initListViews() {

        mRecipeOptions = (Spinner) findViewById(R.id.recipe_spinner);
        mRecipesNamesList = new ArrayList<>();

        // Run a new FetchRecipeTask to set our list with the Recipe data
        new FetchRecipeTask().execute();

        // Populate the Spinner with the Recipe data
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, mRecipesNamesList){
                    // Override the ArrayAdapter to hide the default value/hint "Select"
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {

                        View v = null;

                        if (position == 0) {
                            TextView tv = new TextView(getContext());
                            tv.setHeight(0);
                            tv.setVisibility(View.GONE);
                            v = tv;
                        }
                        else {

                            v = super.getDropDownView(position, null, parent);
                        }

                        parent.setVerticalScrollBarEnabled(false);
                        return v;
                    }
                };

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Add default starting value so Spinner does not show up empty
        adapter.add(getString(R.string.make_selection));

        mRecipeOptions.setAdapter(adapter);

        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mRecipeOptions.getSelectedItemPosition() > 0) {
                    handleOkButton();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.make_selection_prompt), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void handleOkButton() {
        mSelectedRecipe = mRecipesArray[mRecipeOptions.getSelectedItemPosition() - 1];

        // Save the Recipe to SharedPreferences by making it a GSON
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String recipeJson = gson.toJson(mSelectedRecipe);
        prefsEditor.putString(RECIPE_KEY, recipeJson);
        prefsEditor.commit();

        showAppWidget();
    }

    private void showAppWidget() {

        mAppWidgetId = INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(EXTRA_APPWIDGET_ID,
                    INVALID_APPWIDGET_ID);

            AppWidgetProviderInfo providerInfo = AppWidgetManager.getInstance(
                    getBaseContext()).getAppWidgetInfo(mAppWidgetId);
            String appWidgetLabel = providerInfo.label;

            Intent startService = new Intent(ConfigurationActivity.this,
                    WidgetService.class);
            startService.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
            startService.setAction("FROM CONFIGURATION ACTIVITY");
            setResult(RESULT_OK, startService);
            startService(startService);

            finish();
        }
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            Log.i(ConfigurationActivity.class.getSimpleName(), "Invalid app widget ID");
            finish();
        }

    }

    // An AsyncTask to get the Recipe data off the main thread
    private class FetchRecipeTask extends AsyncTask<Recipe[], Recipe[], Recipe[]> {

        @Override
        protected Recipe[] doInBackground(Recipe[]... args) {
            mRecipesArray = QueryUtils.fetchRecipes(getString(R.string.query_url));

            return mRecipesArray;
        }

        @Override
        protected void onPostExecute(Recipe[] result){
            for (int i = 0; i < result.length; i++){
                mRecipesNamesList.add(result[i].getName());
            }
        }

    }
}
