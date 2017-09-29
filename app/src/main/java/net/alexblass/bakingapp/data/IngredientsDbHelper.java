package net.alexblass.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the local database for the stored Ingredients.
 */

public class IngredientsDbHelper extends SQLiteOpenHelper {

    // Database name
    public static final String DATABASE_NAME = "ingredients.db";

    // The version of the database schema
    private static final int DATABASE_VERSION = 9;

    public IngredientsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creation statement
        final String SQL_CREATE_RECIPES_TABLE =
                "CREATE TABLE " + IngredientsContract.IngredientEntry.TABLE_NAME + " (" +
                        IngredientsContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +


                        IngredientsContract.IngredientEntry.COLUMN_RECIPE_ID + " REAL NOT NULL, " +
                        IngredientsContract.IngredientEntry.COLUMN_QUANTITY + " REAL NOT NULL, " +
                        IngredientsContract.IngredientEntry.COLUMN_MEASUREMENT + " TEXT NOT NULL, " +
                        IngredientsContract.IngredientEntry.COLUMN_NAME + " TEXT NOT NULL" +
                        ");";

        db.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + IngredientsContract.IngredientEntry.TABLE_NAME);
        onCreate(db);
    }
}
