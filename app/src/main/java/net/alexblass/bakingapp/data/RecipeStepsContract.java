package net.alexblass.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the RecipeSteps in the offline Recipes database.
 */

public class RecipeStepsContract {

    // The content authority
    public static final String CONTENT_AUTHORITY = "net.alexblass.bakingapp.recipesteps";

    // The base URI for the table
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the table directory
    public static final String PATH_RECIPE_STEPS = "recipesteps";

    // Defines the contents of the table
    public static final class RecipeStepEntry implements BaseColumns {

        // The base CONTENT_URI used to query the RecipeSteps table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE_STEPS)
                .build();

        // The table name
        public static final String TABLE_NAME = "recipesteps";

        // The ID number that references the Recipe
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        // The ID pulled from the network API call,
        // The numerical order of the step
        public static final String COLUMN_RECIPE_STEP_ID = "step_id";

        // The short description of the step
        public static final String COLUMN_SHORT_DESCRIPTION = "step_short_description";

        // The description of the step
        public static final String COLUMN_DESCRIPTION = "step_description";

        // The image URL for the step
        public static final String COLUMN_STEP_IMG_URL = "step_img_url";

        // The video URL for the step
        public static final String COLUMN_STEP_VIDEO_URL = "step_video_url";
    }
}
