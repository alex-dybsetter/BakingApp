package net.alexblass.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the local database for the stored RecipeSteps.
 */

public class RecipeStepsDbHelper extends SQLiteOpenHelper {

    // Database name
    public static final String DATABASE_NAME = "recipesteps.db";

    // The version of the database schema
    private static final int DATABASE_VERSION = 2;

    public RecipeStepsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creation statement
        final String SQL_CREATE_RECIPES_TABLE =
                "CREATE TABLE " + RecipeStepsContract.RecipeStepEntry.TABLE_NAME + " (" +
                        RecipeStepsContract.RecipeStepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RecipeStepsContract.RecipeStepEntry.COLUMN_RECIPE_ID + " REAL NOT NULL, " +
                        RecipeStepsContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID + " INTEGER NOT NULL, " +
                        RecipeStepsContract.RecipeStepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT, " +
                        RecipeStepsContract.RecipeStepEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        RecipeStepsContract.RecipeStepEntry.COLUMN_STEP_IMG_URL + " TEXT NOT NULL, " +
                        RecipeStepsContract.RecipeStepEntry.COLUMN_STEP_VIDEO_URL + " TEXT" +
                        ");";

        db.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
