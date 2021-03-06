package com.udacity.poodlebytes.abnd_project_9;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.udacity.poodlebytes.abnd_project_9.data.BookContract.BookEntry;
import com.udacity.poodlebytes.abnd_project_9.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "Main Activity";
    private static final int NUM_BOOKS = 5;
    private static final int BOOK_LOADER = 1;

    BookCursorAdapter cursorAdapter;

    private BookDbHelper dBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        dBHelper = new BookDbHelper(this);
        Log.i(TAG, "JR On Create");

        // Find the ListView which will be populated with the pet data
        ListView bookListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup the item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Log.i(TAG, "JR = BookLisk onClick OK");
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                Log.i(TAG, "JR = intent set OK");
                startActivity(intent);
            }
        });

        cursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }//end onCreate

    private void insertBooks() {
        Log.i(TAG, "JR Start InsertBooks");
        // Gets the database in write mode
        // SQLiteDatabase db = dBHelper.getWritableDatabase();
        for (int b = 0; b < NUM_BOOKS; b++) {
            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, "Book Title " + b);
            values.put(BookEntry.COLUMN_BOOK_PRICE, b);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, b);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "Supplier Number " + b);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "800.555.000" + b);
            getContentResolver().insert(BookEntry.CONTENT_URI, values);
            Log.i(TAG, "JR book added: " + b);
        }
        Log.i(TAG, "JR End InsertBooks");
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBooks();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}