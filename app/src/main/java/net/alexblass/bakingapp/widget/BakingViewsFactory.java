package net.alexblass.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.Recipe;

import java.util.List;

import static net.alexblass.bakingapp.ConfigurationActivity.PREFS_KEY;
import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * A class to pass data from the ConfigurationActivity to the widget.
 */

public class BakingViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    // The context
    private Context context = null;
    // The app widget
    private int appWidgetId;
    // The selected Recipe
    private Recipe mSelectedRecipe;
    // The Ingredients in the Recipe;
    private List<Ingredient> mIngredients;
    // The shared preferences to store our recipe
    SharedPreferences mPrefs;

    public BakingViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        // Get the saved Recipe from the ConfigurationActivity
        mPrefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(RECIPE_KEY, "");
        mSelectedRecipe = gson.fromJson(json, Recipe.class);
        mIngredients = mSelectedRecipe.getIngredients();
    }

    // Required override method
    @Override
    public void onDestroy() {
    }

    // Required override method
    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    // Required override method
    @Override
    public int getViewTypeCount() {
        return(1);
    }

    // Required override method
    @Override
    public long getItemId(int position) {
        return(position);
    }

    // Required override method
    @Override
    public boolean hasStableIds() {
        return(true);
    }

    // Required override method
    @Override
    public void onDataSetChanged() {
    }

    @Override
    public int getCount() {
        return(mIngredients.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews ingredientListing = new RemoteViews(context.getPackageName(),
        R.layout.widget_row);

        ingredientListing.setTextViewText(R.id.widget_ingredient_name_tv,
                mIngredients.get(position).getIngredientName());
        ingredientListing.setTextViewText(R.id.widget_ingredient_quantity_tv,
                Long.toString(mIngredients.get(position).getQuantity()));
        ingredientListing.setTextViewText(R.id.widget_ingredient_measurement_tv,
                mIngredients.get(position).getMeasurement());

        Intent widgetIntent = new Intent();
        Bundle extras = new Bundle();

        extras.putParcelable(BakingWidgetProvider.INGREDIENT_KEY, mIngredients.get(position));
        widgetIntent.putExtras(extras);
        ingredientListing.setOnClickFillInIntent(R.id.widget_ingredient_name_tv, widgetIntent);
        ingredientListing.setOnClickFillInIntent(R.id.widget_ingredient_quantity_tv, widgetIntent);
        ingredientListing.setOnClickFillInIntent(R.id.widget_ingredient_measurement_tv, widgetIntent);

        return(ingredientListing);
    }
}