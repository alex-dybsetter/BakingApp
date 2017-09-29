package net.alexblass.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Ingredients in the offline Recipes database.
 */

public class IngredientsContract {

    // The content authority
    public static final String CONTENT_AUTHORITY = "net.alexblass.bakingapp.ingredients";

    // The base URI for the table
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the table directory
    public static final String PATH_INGREDIENTS = "ingredients";

    // Defines the contents of the table
    public static final class IngredientEntry implements BaseColumns {

        // The base CONTENT_URI used to query the Ingredients table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS)
                .build();

        // The table name
        public static final String TABLE_NAME = "ingredients";

        // The ID number that references the Recipe
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        // The quantity of the ingredient
        public static final String COLUMN_QUANTITY = "ingredient_quantity";

        // The measurement of the ingredient
        public static final String COLUMN_MEASUREMENT = "ingredient_measurement";

        // The name of the ingredient
        public static final String COLUMN_NAME = "ingredient_name";
    }
}
