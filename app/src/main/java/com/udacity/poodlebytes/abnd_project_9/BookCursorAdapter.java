package com.udacity.poodlebytes.abnd_project_9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.udacity.poodlebytes.abnd_project_9.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class BookCursorAdapter extends CursorAdapter {


    private final static String TAG = "BookCursorAdapter";
    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        final ViewHolder holder = new ViewHolder();
        holder.nameTextView = view.findViewById(R.id.tv_name);
        holder.qtyTextView = view.findViewById(R.id.tv_qty);
        holder.priceTextView = view.findViewById(R.id.tv_price);

        // Find the columns of pet attributes that we're interested in

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int qtyColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        // Read the pet attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQty = cursor.getString(qtyColumnIndex);

        Button saleButton = view.findViewById(R.id.sale);

        // If the price  is empty (should NEVER be!)
        if (TextUtils.isEmpty(bookPrice)) {
            bookPrice = context.getString(R.string.number_unknown);
        }

        holder.nameTextView.setText(bookName);
        holder.qtyTextView.setText(bookQty);
        holder.priceTextView.setText(bookPrice);


        // Change the quantity when you click the button
        saleButton.setOnClickListener(new View.OnClickListener() {
            int currentId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
            Uri contentUri = Uri.withAppendedPath(BookEntry.CONTENT_URI, Integer.toString(currentId));

            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(holder.qtyTextView.getText().toString().trim());
                Log.i(TAG, "JR sale click qty: " + qty);
                if (qty > 0) {
                    qty = qty - 1;
                    Log.i(TAG, "JR new qty: " + qty);
                }
                // Content Values to update quantity
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, qty);

                // update the database
                Log.i(TAG, "JR contentUI " + contentUri.toString());
                context.getContentResolver().update(contentUri, values, null, null);
            }
        });
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView qtyTextView;
        TextView priceTextView;
    }
}