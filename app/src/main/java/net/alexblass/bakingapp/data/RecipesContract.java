package net.alexblass.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the offline Recipes database.
 */

public class RecipesContract {

    // The content authority
    public static final String CONTENT_AUTHORITY = "net.alexblass.bakingapp.recipes";

    // The base URI for the table
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the table directory
    public static final String PATH_RECIPES = "recipes";

    // Defines the contents of the table
    public static final class RecipeEntry implements BaseColumns {

        // The base CONTENT_URI used to query the Recipes table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPES)
                .build();

        // The table name
        public static final String TABLE_NAME = "recipes";

        // The ID pulled from the network
        public static final String COLUMN_RECIPE_SOURCE_ID = "recipe_id";

        // The name of the recipe
        public static final String COLUMN_NAME = "recipe_name";

        // The number of servings in a recipe
        public static final String COLUMN_SERVINGS = "recipe_servings";

        // The image URL for the recipe
        public static final String COLUMN_IMG_URL = "recipe_img_url";

        // Whether or not a Recipe is favorited
        public static final String COLUMN_IS_FAVORITED = "favorites";
    }
}
