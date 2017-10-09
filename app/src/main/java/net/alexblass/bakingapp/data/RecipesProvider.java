package net.alexblass.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * A ContentProvider to access our data.
 */

public class RecipesProvider extends ContentProvider {
    // URI codes
    public static final int CODE_RECIPES_TABLE = 100;
    public static final int CODE_RECIPE_ID = 101;

    private static final int RECIPE_ID_INDEX = 1;

    // URI matcher to help match the codes with the URI
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private RecipesDbHelper mOpenHelper;

    // Tag for error messages
    private static final String LOG_TAG = RecipesProvider.class.getSimpleName();

    // Set the URI codes to determine what data is requested
    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipesContract.CONTENT_AUTHORITY;

        // URI for the database of all recipes
        matcher.addURI(authority, RecipesContract.PATH_RECIPES, CODE_RECIPES_TABLE);

        // URI for the database of a single recipe
        matcher.addURI(authority, RecipesContract.PATH_RECIPES + "/#", CODE_RECIPE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RecipesDbHelper(getContext());
        return true;
    }

    // Insert a single Recipe to the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (match) {
            case CODE_RECIPES_TABLE:
                // Insert the Recipe
                long id = db.insert(RecipesContract.RecipeEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

                // Notify all listeners that the data has changed
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Query the database for all the information or recipes by ID
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

         // Determine which URI to use
        switch (match){
            // Get the Recipe by its ID
            case CODE_RECIPE_ID:
                String recipeIdString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{recipeIdString};

                cursor = db.query(
                        RecipesContract.RecipeEntry.TABLE_NAME,
                        projection,
                        RecipesContract.RecipeEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_RECIPES_TABLE:
                cursor = db.query(
                        RecipesContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Delete a Recipe from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowDeleted;

        switch (match){

            // Get the ID for a single recipe and delete it
            case CODE_RECIPE_ID:
                String recipeId = uri.getPathSegments().get(RECIPE_ID_INDEX);
                rowDeleted = db.delete(
                        RecipesContract.RecipeEntry.TABLE_NAME,
                        RecipesContract.RecipeEntry._ID + "=?",
                        new String[] {recipeId}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    // Update an entry in the database
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count = 0;
        switch (match) {
            // Update the whole table
            case CODE_RECIPES_TABLE:
                count = db.update(RecipesContract.RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            // Update individual table rows
            case CODE_RECIPE_ID:
                count = db.update(RecipesContract.RecipeEntry.TABLE_NAME,
                        values,
                        RecipesContract.RecipeEntry._ID + "=" +
                                uri.getPathSegments().get(RECIPE_ID_INDEX) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    // Required override method
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Method not implemented by Baking Time.");
    }
}
