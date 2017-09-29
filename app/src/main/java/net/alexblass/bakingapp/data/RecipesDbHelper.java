package net.alexblass.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the local database for the stored Recipes.
 */

public class RecipesDbHelper extends SQLiteOpenHelper {

    // Database name
    public static final String DATABASE_NAME = "recipes.db";

    // The version of the database schema
    private static final int DATABASE_VERSION = 8;

    public RecipesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creation statement
        final String SQL_CREATE_RECIPES_TABLE =
                "CREATE TABLE " + RecipesContract.RecipeEntry.TABLE_NAME + " (" +
                        RecipesContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        // Locally added recipes won't have a source ID since they are not pulled
                        // from a network source so this does not require a "NOT NULL" value.
                        RecipesContract.RecipeEntry.COLUMN_RECIPE_SOURCE_ID + " INTEGER, " +

                        RecipesContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        RecipesContract.RecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, " +
                        RecipesContract.RecipeEntry.COLUMN_IMG_URL + " TEXT, " +
                        RecipesContract.RecipeEntry.COLUMN_IS_FAVORITED + " INTEGER DEFAULT 0" +
                ");";

        db.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
