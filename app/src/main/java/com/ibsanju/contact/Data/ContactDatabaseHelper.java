package com.ibsanju.contact.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ibsanju.contact.Data.ContactContract.ContactEntry;


public
class ContactDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contact.db";

    private static final int DATABASE_VERSION = 1;

    public ContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating table
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + ContactEntry.TABLE_NAME + "(" +
                        ContactEntry._ID + " integer primary key, " +
                        ContactEntry.COLUMN_FirstNAME + " TEXT, " +
                        ContactEntry.COLUMN_LastNAME + " TEXT, " +
                        ContactEntry.COLUMN_PHONE + " INTEGER, " +
                        ContactEntry.COLUMN_EMAIL + " TEXT, " +
                        ContactEntry.COLUMN_STREET + " TEXT, " +
                        ContactEntry.COLUMN_CITY + " TEXT, " +
                        ContactEntry.COLUMN_STATE + " TEXT, " +
                        ContactEntry.COLUMN_ZIP + " TEXT);";
        db.execSQL(CREATE_CONTACTS_TABLE); // create the contact table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
