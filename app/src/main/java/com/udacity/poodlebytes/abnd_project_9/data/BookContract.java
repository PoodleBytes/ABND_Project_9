package com.udacity.poodlebytes.abnd_project_9.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Books app.
 */
public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.poodlebytes.abnd_project_9";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";


    //  prevent accidentally instantiating the class,
    private BookContract() {
        throw new AssertionError("No BookContract instances for you!");
    }

    /**
     * Inner class that defines constant values for the Books database table.
     */
    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books & single book
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * DEFINE DATABASE SCHEMA
         */
        public final static String TABLE_NAME = "books";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "title";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_SUPPLIER = "supplier";
        public final static String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";
    }
}