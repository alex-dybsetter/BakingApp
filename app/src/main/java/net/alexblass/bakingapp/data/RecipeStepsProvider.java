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

public class RecipeStepsProvider extends ContentProvider {
    // URI codes
    public static final int CODE_STEPS_TABLE = 100;
    public static final int CODE_STEP_ID = 101;

    private static final int STEP_ID_INDEX = 1;

    // URI matcher to help match the codes with the URI
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private RecipeStepsDbHelper mOpenHelper;

    // Tag for error messages
    private static final String LOG_TAG = RecipeStepsProvider.class.getSimpleName();

    // Set the URI codes to determine what data is requested
    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipeStepsContract.CONTENT_AUTHORITY;

        // URI for the database of all RecipeSteps
        matcher.addURI(authority, RecipeStepsContract.PATH_RECIPE_STEPS, CODE_STEPS_TABLE);

        // URI for the database of a single RecipeStep
        matcher.addURI(authority, RecipeStepsContract.PATH_RECIPE_STEPS + "/#", CODE_STEP_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RecipeStepsDbHelper(getContext());
        return true;
    }

    // Insert a single RecipeStep to the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (match) {
            case CODE_STEPS_TABLE:
                // Insert the RecipeStep
                long id = db.insert(RecipeStepsContract.RecipeStepEntry.TABLE_NAME, null, values);
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

    // Query the database for all the RecipeSteps by ID
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        // Determine which URI to use
        switch (match){
            // Get the RecipeStep by its ID
            case CODE_STEP_ID:
                String stepIdString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{stepIdString};

                cursor = db.query(
                        RecipeStepsContract.RecipeStepEntry.TABLE_NAME,
                        projection,
                        RecipeStepsContract.RecipeStepEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_STEPS_TABLE:
                cursor = db.query(
                        RecipeStepsContract.RecipeStepEntry.TABLE_NAME,
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

    // Delete a RecipeStep from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowDeleted;

        switch (match){

            // Get the ID for a single RecipeStep and delete it
            case CODE_STEP_ID:
                String stepId = uri.getPathSegments().get(STEP_ID_INDEX);
                rowDeleted = db.delete(
                        RecipeStepsContract.RecipeStepEntry.TABLE_NAME,
                        RecipeStepsContract.RecipeStepEntry._ID + "=?",
                        new String[] {stepId}
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
            case CODE_STEPS_TABLE:
                count = db.update(RecipeStepsContract.RecipeStepEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            // Update individual table rows
            case CODE_STEP_ID:
                count = db.update(RecipeStepsContract.RecipeStepEntry.TABLE_NAME,
                        values,
                        RecipesContract.RecipeEntry._ID + "=" +
                                uri.getPathSegments().get(STEP_ID_INDEX) +
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