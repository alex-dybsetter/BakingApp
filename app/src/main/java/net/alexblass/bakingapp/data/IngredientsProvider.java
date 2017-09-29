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

public class IngredientsProvider extends ContentProvider {
    // URI codes
    public static final int CODE_INGREDIENTS_TABLE = 100;
    public static final int CODE_INGREDIENTS_ID = 101;

    private static final int INGREDIENT_ID_INDEX = 1;

    // URI matcher to help match the codes with the URI
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private IngredientsDbHelper mOpenHelper;

    // Tag for error messages
    private static final String LOG_TAG = IngredientsProvider.class.getSimpleName();

    // Set the URI codes to determine what data is requested
    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = IngredientsContract.CONTENT_AUTHORITY;

        // URI for the database of all Ingredients
        matcher.addURI(authority, IngredientsContract.PATH_INGREDIENTS, CODE_INGREDIENTS_TABLE);

        // URI for the database of a single Ingredient
        matcher.addURI(authority, IngredientsContract.PATH_INGREDIENTS + "/#", CODE_INGREDIENTS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new IngredientsDbHelper(getContext());
        return true;
    }

    // Insert a single Ingredient to the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (match) {
            case CODE_INGREDIENTS_TABLE:
                // Insert the Ingredient
                long id = db.insert(IngredientsContract.IngredientEntry.TABLE_NAME, null, values);
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

    // Query the database for all the ingredients by ID
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        // Determine which URI to use
        switch (match){
            // Get the Ingredient by its ID
            case CODE_INGREDIENTS_ID:
                String ingredientIdString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{ingredientIdString};

                cursor = db.query(
                        IngredientsContract.IngredientEntry.TABLE_NAME,
                        projection,
                        IngredientsContract.IngredientEntry._ID + "=? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_INGREDIENTS_TABLE:
                cursor = db.query(
                        IngredientsContract.IngredientEntry.TABLE_NAME,
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

    // Delete a Ingredient from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowDeleted;

        switch (match){

            // Get the ID for a single Ingredient and delete it
            case CODE_INGREDIENTS_ID:
                String ingredientId = uri.getPathSegments().get(INGREDIENT_ID_INDEX);
                rowDeleted = db.delete(
                        IngredientsContract.IngredientEntry.TABLE_NAME,
                        IngredientsContract.IngredientEntry._ID + "=?",
                        new String[] {ingredientId}
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
            case CODE_INGREDIENTS_TABLE:
                count = db.update(IngredientsContract.IngredientEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            // Update individual table rows
            case CODE_INGREDIENTS_ID:
                count = db.update(IngredientsContract.IngredientEntry.TABLE_NAME,
                        values,
                        IngredientsContract.IngredientEntry._ID + "=" +
                                uri.getPathSegments().get(INGREDIENT_ID_INDEX) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
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
