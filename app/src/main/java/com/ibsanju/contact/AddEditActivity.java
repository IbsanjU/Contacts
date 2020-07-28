package com.ibsanju.contact;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.ibsanju.contact.Data.ContactContract.ContactEntry;
import com.ibsanju.contact.Data.ContactData;

import java.util.regex.Pattern;

public
class AddEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_CONTACT_LOADER = 0;

    ContactData cd;
    private Uri mCurrentContactUri;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mPhoneEditText;
    private EditText mEmailEditText;
    private EditText mStreetEditText;
    private EditText mCityEditText;
    private EditText mStateEditText;
    private EditText mZipEditText;

    private boolean mContactHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mContactHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        cd = new ContactData();
        mFirstNameEditText = findViewById(R.id.et_FN);
        mLastNameEditText = findViewById(R.id.et_LN);
        mPhoneEditText = findViewById(R.id.et_phone);
        mEmailEditText = findViewById(R.id.et_email);
        mStreetEditText = findViewById(R.id.et_street);
        mCityEditText = findViewById(R.id.et_city);
        mStateEditText = findViewById(R.id.et_state);
        mZipEditText = findViewById(R.id.et_zip);

        mFirstNameEditText.setOnTouchListener(mTouchListener);
        mLastNameEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mStreetEditText.setOnTouchListener(mTouchListener);
        mCityEditText.setOnTouchListener(mTouchListener);
        mStateEditText.setOnTouchListener(mTouchListener);
        mZipEditText.setOnTouchListener(mTouchListener);

        if (mCurrentContactUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_contact));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_contact));

            invalidateOptionsMenu();

            LoaderManager.getInstance(this).initLoader(EXISTING_CONTACT_LOADER, null, this);
        }
    }

    /**
     * Get user input from editor and save new contact into database.
     */
    private boolean saveContact() {
        String fnameString = mFirstNameEditText.getText().toString().trim();
        String lnameString = mLastNameEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();
        String streetString = mStreetEditText.getText().toString().trim();
        String cityString = mCityEditText.getText().toString().trim();
        String stateString = mStateEditText.getText().toString().trim();
        String zipString = mZipEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fnameString) || TextUtils.isEmpty(lnameString)
                || TextUtils.isEmpty(phoneString) || !isValidMobile(phoneString)
                || TextUtils.isEmpty(emailString) || !isValidMail(emailString)) {

            if (TextUtils.isEmpty(fnameString)) {
                mFirstNameEditText.setError(getString(R.string.error_edittext));
                mFirstNameEditText.requestFocus();
            } else if (TextUtils.isEmpty(lnameString)) {
                mLastNameEditText.setError(getString(R.string.error_edittext));
                mLastNameEditText.requestFocus();
            } else if (TextUtils.isEmpty(phoneString) || !isValidMobile(phoneString)) {
                mPhoneEditText.setError(getString(R.string.valid_number));
                mPhoneEditText.requestFocus();
            } else if (TextUtils.isEmpty(emailString) || !isValidMail(emailString)) {
                mEmailEditText.setError(getString(R.string.valid_email));
                mEmailEditText.requestFocus();
            }
            return false;
        } else {

            ContentValues values = new ContentValues();
            values.put(ContactEntry.COLUMN_FirstNAME, fnameString);
            values.put(ContactEntry.COLUMN_LastNAME, lnameString);
            values.put(ContactEntry.COLUMN_PHONE, phoneString);
            values.put(ContactEntry.COLUMN_EMAIL, emailString);
            values.put(ContactEntry.COLUMN_STREET, streetString);
            values.put(ContactEntry.COLUMN_CITY, cityString);
            values.put(ContactEntry.COLUMN_STATE, stateString);
            values.put(ContactEntry.COLUMN_ZIP, zipString);

            if (mCurrentContactUri == null) {
                Uri newUri = getContentResolver().insert(ContactEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_contact_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_contact_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentContactUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_contact_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_contact_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }

    }

    private boolean isValidMail(String email) {
        return Pattern.matches(getString(R.string.regx_email), email);
//        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {
        return Pattern.matches(getString(R.string.regex_phone), phone);
//        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new contact, hide the "Delete" menu item.
        if (mCurrentContactUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save contact to database
//                saveContact();
                // Exit activity
                if (saveContact()) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the contact hasn't changed, continue with navigating up to parent activity
                if (!mContactHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                    return true;
                }

                /*
                 Otherwise if there are unsaved changes, setup a dialog to warn the user.
                 Create a click listener to handle the user confirming that
                 changes should be discarded.
                */
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the contact hasn't changed, continue with handling back button press
        if (!mContactHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the contact.
                deleteContact();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the contact.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all contact attributes, define a projection that contains
        // all columns from the contact table
        String[] projection = {
                ContactEntry._ID,
                ContactEntry.COLUMN_FirstNAME,
                ContactEntry.COLUMN_LastNAME,
                ContactEntry.COLUMN_PHONE,
                ContactEntry.COLUMN_EMAIL,
                ContactEntry.COLUMN_STREET,
                ContactEntry.COLUMN_CITY,
                ContactEntry.COLUMN_STATE,
                ContactEntry.COLUMN_ZIP
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentContactUri,         // Query the content URI for the current contact
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int fnameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_FirstNAME);
            int lnameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_LastNAME);
            int phoneColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_EMAIL);
            int streetColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_STREET);
            int cityColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_CITY);
            int stateColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_STATE);
            int zipColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_ZIP);

            String fname = cursor.getString(fnameColumnIndex);
            String lname = cursor.getString(lnameColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String street = cursor.getString(streetColumnIndex);
            String city = cursor.getString(cityColumnIndex);
            String state = cursor.getString(stateColumnIndex);
            String zip = cursor.getString(zipColumnIndex);

            mFirstNameEditText.setText(fname);
            mLastNameEditText.setText(lname);
            mPhoneEditText.setText(phone);
            mEmailEditText.setText(email);
            mStreetEditText.setText(street);
            mCityEditText.setText(city);
            mStateEditText.setText(state);
            mZipEditText.setText(zip);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mFirstNameEditText.setText("");
        mLastNameEditText.setText("");
        mPhoneEditText.setText("");
        mEmailEditText.setText("");
        mStateEditText.setText("");
        mCityEditText.setText("");
        mZipEditText.setText("");
        mStreetEditText.setText("");

    }

    private void deleteContact() {
        if (mCurrentContactUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentContactUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}