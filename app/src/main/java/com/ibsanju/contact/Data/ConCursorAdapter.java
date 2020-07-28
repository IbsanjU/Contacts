package com.ibsanju.contact.Data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.ibsanju.contact.Data.ContactContract.ContactEntry;
import com.ibsanju.contact.R;


public
class ConCursorAdapter extends CursorAdapter {

    public ConCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.tv_fullname);
        TextView phoneTextView = view.findViewById(R.id.tv_phone);

        int fnameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_FirstNAME);
        int lnameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_LastNAME);
        int phoneColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_PHONE);

        String confName = cursor.getString(fnameColumnIndex);
        String conlName = cursor.getString(lnameColumnIndex);
        String conPhone = cursor.getString(phoneColumnIndex);

        nameTextView.setText(confName + " " + conlName);
        phoneTextView.setText(conPhone);
    }
}
