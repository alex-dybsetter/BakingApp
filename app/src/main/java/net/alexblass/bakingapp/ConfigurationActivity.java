package net.alexblass.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.QueryUtils;
import net.alexblass.bakingapp.utilities.WidgetService;

import java.util.ArrayList;
import java.util.List;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

/**
 * An Activity that allows users to configure their widget settings to choose a Recipe.
 */

public class ConfigurationActivity extends Activity {

    // An Array of Recipes pulled from the network JSON url
    Recipe[] mRecipesArray;
    // A list of the Recipe names
    List<String> mRecipesNamesList;
    // The spinner of Recipe options
    Spinner mRecipeOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        setResult(RESULT_CANCELED);

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
        mRecipeOptions.setSelection(0);

        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                handleOkButton();
            }
        });

    }

    // TODO: Pass selected Recipe data to widget
    // TODO: Display Recipe ingredients in ListView widget

    private void handleOkButton() {
        showAppWidget();
    }

    int mAppWidgetId;

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
            Log.i("I am invalid", "I am invalid");
            finish();
        }

    }

    // An AsyncTask to get the Recipe data off the main thread
    class FetchRecipeTask extends AsyncTask<Recipe[], Recipe[], Recipe[]> {

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
