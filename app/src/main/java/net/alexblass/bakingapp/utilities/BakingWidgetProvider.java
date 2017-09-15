package net.alexblass.bakingapp.utilities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;

import net.alexblass.bakingapp.MainActivity;
import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Recipe;

import static net.alexblass.bakingapp.ConfigurationActivity.PREFS_KEY;
import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * A WidgetProvider to display a custom widget on the home screen.
 */

public class BakingWidgetProvider extends AppWidgetProvider {

    // The key to pass the Ingredient to the widget
    public static final String INGREDIENT_KEY = "ingredient";
    // The shared preferences to store our recipe
    SharedPreferences mPrefs;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get the saved Recipe from the ConfigurationActivity
        mPrefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(RECIPE_KEY, "");
        Recipe recipe = gson.fromJson(json, Recipe.class);

        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, WidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(),
                    R.layout.widget);

            widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_ingredients_list,
                    intent);

            widget.setTextViewText(R.id.widget_recipe_title, recipe.getName());

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPI=PendingIntent
                    .getActivity(context, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.widget_ingredients_list, clickPI);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
