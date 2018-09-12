package com.udacity.poodlebytes.abnd_project_9;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.udacity.poodlebytes.abnd_project_9.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EditorActivity";
    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 1;
    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri currentBookUri;
    /**
     * EditText fields
     */
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText qtyEditText;
    private EditText supplierEditText;
    private EditText supplierPhoneEditText;
    private Button addOneBook;
    private Button deleteOneBook;
    private Button callSupplier;


    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean bookHasChanged = false;


    /**
     * listens for any user touches
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create and set the message for AlertDialog.Builder and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // If the intent DOES NOT contain a content URI, then creating a new entry.
        if (currentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the pet data from the database
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find  relevant views to read user input
        nameEditText = findViewById(R.id.edit_book_name);
        priceEditText = findViewById(R.id.edit_book_price);
        qtyEditText = findViewById(R.id.edit_book_qty);
        supplierEditText = findViewById(R.id.edit_book_supplier);
        supplierPhoneEditText = findViewById(R.id.edit_book_supplier_phone);
        addOneBook = findViewById(R.id.plus_one);
        deleteOneBook = findViewById(R.id.minus_one);
        callSupplier = findViewById(R.id.call);


        // Setup OnTouchListeners on all the input fields
        nameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        qtyEditText.setOnTouchListener(mTouchListener);
        supplierEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);

        addOneBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int qty = 0;
                String strQty = qtyEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(strQty)) {
                    qty = Integer.parseInt(strQty);
                }
                qty = qty + 1;
                qtyEditText.setText(Integer.toString(qty));
            }
        });

        deleteOneBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int qty = 0;
                String strQty = qtyEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(strQty)) {
                    qty = Integer.parseInt(strQty);
                }
                if (qty > 0) {
                    qty = qty - 1;
                }
                qtyEditText.setText(Integer.toString(qty));
            }
        });

        callSupplier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callPhone();
            }
        });
    }//end onCreate

    private void saveBook() {
        // Read from input fields
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String qtyString = qtyEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        if (currentBookUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(qtyString) || TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(getApplicationContext(), "Please, fill in the blank fields.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        int qty = 0;
        if (!TextUtils.isEmpty(qtyString)) {
            qty = Integer.parseInt(qtyString);
        }
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, qty);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneString);

        if (currentBookUri == null) {  //new record
            // Insert a new book
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                //  content URI is null = error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // insertion was successful
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {              //update record
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            if (rowsAffected == 0) {
                //  no rows were affected =  error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // update successful
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }//end saveBook

    private void deleteBook() {
        Log.i(TAG, "JR - start deleteBook");
        // Only perform the delete if this is an existing book.
        if (currentBookUri != null) {
            // Call the ContentResolver to delete the book for the given content URI.
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                //  no rows were deleted =  error with  delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // delete was successful
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    private AlertDialog AskOption() {       //https://stackoverflow.com/questions/11740311/android-confirmation-message-for-delete
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(getString(R.string.action_delete))
                .setMessage(getString(R.string.action_delete_book))
                .setIcon(R.drawable.delete)
                .setPositiveButton(getString(R.string.action_delete), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteBook();
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel_delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // new record = hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to "Save" menu option
            case R.id.action_save:
                // Save record
                saveBook();
                return true;
            // Respond to "Delete" menu option
            case R.id.action_delete:
                AlertDialog diaBox = AskOption();
                diaBox.show();
                return true;
            // Respond to "Up" arrow button in the app bar
            case android.R.id.home:
                // If the record hasn't changed, continue
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //  projection that contains all columns from the table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, currentBookUri, projection, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            // Find the columns of attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            if (TextUtils.isEmpty(supplier)) {
                supplier = getString(R.string.supplier_unknown);
            }
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            if (TextUtils.isEmpty(supplierPhone)) {
                supplierPhone = getString(R.string.supplier_phone_unknown);
            }
            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            qtyEditText.setText(Integer.toString(qty));

            supplierEditText.setText(supplier);
            supplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.setText("");
        priceEditText.setText("");
        qtyEditText.setText("");
        supplierEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        // If the record hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Create a click listener to handle the user confirming that changes should be discarded.
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

    private void callPhone() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + supplierPhoneEditText.getText().toString().trim()));
            if (callIntent.resolveActivity(getPackageManager()) != null) { //submission 2 correction
                startActivity(callIntent);
            }
            return;
        }
    }
}//end editor activity