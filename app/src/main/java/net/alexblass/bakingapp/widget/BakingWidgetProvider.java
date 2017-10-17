package net.alexblass.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import net.alexblass.bakingapp.ConfigurationActivity;
import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.RecipeOverviewActivity;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.utilities.RecipeQueryUtils;

import static net.alexblass.bakingapp.data.constants.Keys.PREFS_KEY;
import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_KEY;
import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_NAME_KEY;
import static net.alexblass.bakingapp.data.constants.Keys.WIDGET_ID_KEY;

/**
 * A WidgetProvider to display a custom widget on the home screen.
 */

public class BakingWidgetProvider extends AppWidgetProvider {

    // The shared preferences to store our recipe
    SharedPreferences mPrefs;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {

            Intent intent = new Intent(context, WidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);

            widget.setRemoteAdapter(widgetId, R.id.widget_ingredients_list, intent);

            // At the time of widget creation when this method is called, mPrefs does not have the
            // recipe data for the widget so we need to set the recipe data in the WidgetService
            // class to display the recipe correctly for the FIRST time.  When the user launches the
            // config activity to change the recipe, we can use this block of code to UPDATE the
            // recipe information for an EXISTING widget.
            mPrefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            int recipeId = mPrefs.getInt("widget" + widgetId, -1);
            if (recipeId > 0){
                Recipe recipe = RecipeQueryUtils.getRecipe(context, recipeId);

                widget.setTextViewText(R.id.widget_recipe_title, recipe.getName());

                // Launch the ConfigurationActivity when the user clicks the gear button
                Intent configIntent = new Intent(context, ConfigurationActivity.class);
                configIntent.putExtra(RECIPE_NAME_KEY, recipe.getName());
                configIntent.putExtra(WIDGET_ID_KEY, widgetId);
                PendingIntent configPI = PendingIntent
                        .getActivity(context, widgetId, configIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                widget.setOnClickPendingIntent(R.id.widget_settings_btn, configPI);

                // Launch the app when the user clicks the launcher button of the widget
                Intent recipeIntent = new Intent(context, RecipeOverviewActivity.class);
                recipeIntent.putExtra(RECIPE_KEY, recipe);
                PendingIntent recipePI = PendingIntent
                        .getActivity(context, widgetId, recipeIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                widget.setOnClickPendingIntent(R.id.widget_launch_btn, recipePI);
            }

            appWidgetManager.updateAppWidget(widgetId, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // Delete the SharedPreference file for the deleted widget
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        mPrefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        for (int widgetId : appWidgetIds) {
            prefsEditor.remove("widget" + widgetId);
        }
        prefsEditor.apply();
    }

    // Update the ListView in the widget
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, BakingWidgetProvider.class);
            manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(componentName), R.id.widget_ingredients_list);
        }
        super.onReceive(context, intent);
    }
}
