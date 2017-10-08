package net.alexblass.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import net.alexblass.bakingapp.ConfigurationActivity;
import net.alexblass.bakingapp.R;

import static net.alexblass.bakingapp.data.constants.Keys.PREFS_KEY;

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

        for (int i = 0; i < appWidgetIds.length; i++) {

            Intent intent = new Intent(context, WidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(),
                    R.layout.widget);

            widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_ingredients_list,
                    intent);

            // Launch the ConfigurationActivity when the user clicks the gear button
            Intent configIntent = new Intent(context, ConfigurationActivity.class);
            PendingIntent configPI = PendingIntent
                    .getActivity(context, 0,
                            configIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setOnClickPendingIntent(R.id.widget_settings_btn, configPI);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        mPrefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        for (int i = 0; i < appWidgetIds.length; i++) {
            prefsEditor.remove("widget" + appWidgetIds[i]);
        }
        prefsEditor.commit();
    }
}
