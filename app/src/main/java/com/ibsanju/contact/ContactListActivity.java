package com.ibsanju.contact;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ibsanju.contact.Data.ConCursorAdapter;
import com.ibsanju.contact.Data.ContactContract.ContactEntry;

public
class ContactListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CONTACT_LOADER = 0;
    ConCursorAdapter mCursorAdapter;
    ListView conListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, AddEditActivity.class);
                startActivity(intent);
            }
        });

        conListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        conListView.setEmptyView(emptyView);

        mCursorAdapter = new ConCursorAdapter(this, null);
        conListView.setAdapter(mCursorAdapter);

        //Setup item Click listener
        conListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ContactListActivity.this, AddEditActivity.class);

                Uri currentContactUri = ContentUris.withAppendedId(ContactEntry.CONTENT_URI, id);

                intent.setData(currentContactUri);

                startActivity(intent);
            }
        });
        //Kick off the loader
        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
    }


    private void insertContact() {
        ContentValues values = new ContentValues();
        values.put(ContactEntry.COLUMN_FirstNAME, "Bharath");
        values.put(ContactEntry.COLUMN_LastNAME, "Kumar");
        values.put(ContactEntry.COLUMN_PHONE, "+1987654321");
        values.put(ContactEntry.COLUMN_EMAIL, "bharath2668@gmail.com");

        Uri newUri = getContentResolver().insert(ContactEntry.CONTENT_URI, values);
    }

    private void deleteAllContacts() {
        int rowsDeleted = getContentResolver().delete(ContactEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from contact database");
        Toast.makeText(getApplicationContext(), rowsDeleted + " rows deleted from contact database",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertContact();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllContacts();
                return true;
            case R.id.app_bar_search:
                Intent intent = new Intent(ContactListActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_FirstNAME,
                ContactEntry.COLUMN_LastNAME,
                ContactEntry.COLUMN_PHONE};

        return new CursorLoader(this,   // Parent activity Context
                ContactEntry.CONTENT_URI,           // The content URI of the words table
                projection,                     // The columns to return for each row
                null,                   // Either null, or the word the user entered
                null,               // Either empty, or the string the user entered
                ContactEntry.COLUMN_FirstNAME + " ASC");                  // The sort order for the returned rows
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link conCursorAdapter with this new cursor containing updated contact data
        TextView countTextView = findViewById(R.id.tv_count);
        countTextView.setText("[ Total Contacts: " + data.getCount() + " ]");
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }

}