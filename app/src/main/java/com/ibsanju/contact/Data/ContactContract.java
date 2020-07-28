package com.ibsanju.contact.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final
class ContactContract {
    public static final String CONTENT_AUTHORITY = "com.ibsanju.contact";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CONTACTS = "contacts";

    private ContactContract() {
    }

    public static final
    class ContactEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACTS);

        public final static String TABLE_NAME = "contacts";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_FirstNAME = "FirstName";
        public final static String COLUMN_LastNAME = "LastName";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single contact.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String CONTENT_SEARCH =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;


    }
}
