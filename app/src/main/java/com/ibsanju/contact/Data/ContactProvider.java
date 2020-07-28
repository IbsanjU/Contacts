package com.ibsanju.contact.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ibsanju.contact.Data.ContactContract.ContactEntry;

public
class ContactProvider extends ContentProvider {

    public static final String LOG_TAG = ContactProvider.class.getSimpleName();
    private static final int CONTACTS = 100;
    private static final int CONTACT_ID = 101;
    private static final int CONTACT_SEARCH = 102;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS, CONTACTS);
        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS + "/#", CONTACT_ID);
        sUriMatcher.addURI(ContactContract.CONTENT_AUTHORITY, ContactContract.PATH_CONTACTS + "/s", CONTACT_SEARCH);
    }

    /**
     * Database helper Object
     */
    private ContactDatabaseHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ContactDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                cursor = database.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CONTACT_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CONTACT_SEARCH:
                selection = ContactEntry.COLUMN_FirstNAME + " LIKE %?% OR " + ContactEntry.COLUMN_LastNAME + " LIKE %?% OR " + ContactEntry.COLUMN_PHONE + " LIKE %?%";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return insertContact(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertContact(Uri uri, ContentValues values) {

        // TODO: Insert a new contact into the contacts database table with the given ContentValues
        String name = values.getAsString(ContactEntry.COLUMN_FirstNAME);
        if (name == null) {
            throw new IllegalArgumentException("Contact requires a name");
        }
        String phone = values.getAsString(ContactEntry.COLUMN_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Contact requires a phone number");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new contact with the given values
        long id = database.insert(ContactEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return ContentUris.withAppendedId(uri, id);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return updateContact(uri, contentValues, selection, selectionArgs);
            case CONTACT_ID:
                // For the CONTACT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateContact(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateContact(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ContactEntry.COLUMN_FirstNAME)) {
            String name = values.getAsString(ContactEntry.COLUMN_FirstNAME);
            if (name == null) {
                throw new IllegalArgumentException("Contact requires a name");
            }
        }
        if (values.containsKey(ContactEntry.COLUMN_PHONE)) {
            String name = values.getAsString(ContactEntry.COLUMN_PHONE);
            if (name == null) {
                throw new IllegalArgumentException("Contact requires a phone number");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ContactEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                // Delete a single row given by the ID in the URI
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return ContactEntry.CONTENT_LIST_TYPE;
            case CONTACT_ID:
                return ContactEntry.CONTENT_ITEM_TYPE;
            case CONTACT_SEARCH:
                return ContactEntry.CONTENT_SEARCH;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


}
