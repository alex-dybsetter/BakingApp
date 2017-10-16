package net.alexblass.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.RecipeOverviewActivity;
import net.alexblass.bakingapp.utilities.RecipeQueryUtils;
import net.alexblass.bakingapp.models.Recipe;

import static net.alexblass.bakingapp.data.constants.Keys.PREFS_KEY;
import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_KEY;

public class WidgetService extends RemoteViewsService {

    // The shared preferences that has our Recipe from the ConfigActivity
    SharedPreferences mPrefs;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

        // Get the saved Recipe from the ConfigurationActivity
        mPrefs = getApplicationContext().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        int recipeId = mPrefs.getInt("widget" + mAppWidgetId, -1);
        Recipe recipe = RecipeQueryUtils.getRecipe(getApplicationContext(), recipeId);

        // Set the Recipe title in the widget
        RemoteViews widget = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget);
        widget.setTextViewText(R.id.widget_recipe_title, recipe.getName());

        // Launch the app when the user clicks the launcher button of the widget
        Intent recipeIntent = new Intent(getApplicationContext(), RecipeOverviewActivity.class);
        recipeIntent.putExtra(RECIPE_KEY, recipe);
        PendingIntent recipePI = PendingIntent
                .getActivity(getApplicationContext(), 0,
                        recipeIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.widget_ingredients_list, recipePI);
        widget.setOnClickPendingIntent(R.id.widget_launch_btn, recipePI);

        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        manager.updateAppWidget(mAppWidgetId, widget);

        return(new BakingViewsFactory(this.getApplicationContext(),
                intent));
    }
}