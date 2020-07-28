package com.ibsanju.contact;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.ibsanju.contact.Data.ConCursorAdapter;
import com.ibsanju.contact.Data.ContactContract.ContactEntry;

public
class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CONTACT_LOADER = 0;
    ConCursorAdapter mCursorAdapter;
    ListView conListView;
    String[] selectionArgs;
    String selectionClause;
    String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.editor_activity_title_edit_search);

        conListView = findViewById(R.id.slist);

        View emptyView = findViewById(R.id.empty_view_search);
        conListView.setEmptyView(emptyView);

        mCursorAdapter = new ConCursorAdapter(this, null);
        conListView.setAdapter(mCursorAdapter);

        //Setup item Click listener
        conListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, AddEditActivity.class);

                Uri currentContactUri = ContentUris.withAppendedId(ContactEntry.CONTENT_URI, id);

                intent.setData(currentContactUri);

                startActivity(intent);
            }
        });
        //Kick off the loader
        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint("Search Here!");
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
//                Toast.makeText(SearchActivity.this, newText, Toast.LENGTH_LONG).show();
                return true;
            }
        });
        return true;
    }

    private void search(String text) {

        selectionArgs = new String[]{""};
        sortOrder = ContactEntry.COLUMN_FirstNAME + " ASC";

        if (text.equals("")) {
            // Setting the selection clause to null will return all words
            selectionClause = null;
            selectionArgs = null;
        } else {
            // Constructs a selection clause that matches the word that the user entered.
            selectionClause = ContactEntry.COLUMN_FirstNAME + " LIKE ? OR "
                    + ContactEntry.COLUMN_LastNAME + " LIKE ? OR "
                    + ContactEntry.COLUMN_PHONE + " LIKE ?";
            // Moves the user's input string to the selection arguments.
            selectionArgs = new String[]{"%" + text + "%", "%" + text + "%", "%" + text + "%"};
        }
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_FirstNAME,
                ContactEntry.COLUMN_LastNAME,
                ContactEntry.COLUMN_PHONE};
        try {
            Cursor mCursor = getContentResolver().query(
                    ContactEntry.CONTENT_URI,
                    projection,
                    selectionClause,
                    selectionArgs,
                    sortOrder);
            mCursorAdapter = new ConCursorAdapter(this, mCursor);
            conListView.setAdapter(mCursorAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.app_bar_search:
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
                selectionClause,                   // Either null, or the word the user entered
                selectionArgs,               // Either empty, or the string the user entered
                sortOrder);                  // The sort order for the returned rows
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link conCursorAdapter with this new cursor containing updated contact data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }

}