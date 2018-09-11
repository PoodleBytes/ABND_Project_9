package com.udacity.poodlebytes.abnd_project_9;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.udacity.poodlebytes.abnd_project_9.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an {@link Cursor} adapter
 */

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
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
     * @param cursor  cursor data of correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * binds the cursor to the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.tv_name);
        TextView summaryTextView = view.findViewById(R.id.tv_detail);

        // Find the columns attributes
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int qtyColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        // Read the attributes from the Cursor
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQty = cursor.getString(qtyColumnIndex);

        // If the price  is empty (should NEVER be!)
        if (TextUtils.isEmpty(bookPrice)) {
            bookPrice = context.getString(R.string.number_unknown);
        }

        // Update the TextViews
        StringBuilder detail = new StringBuilder();
        detail.append("Price: ");
        detail.append(bookPrice);
        detail.append("  Qty On Hand: ");
        detail.append(bookQty);
        nameTextView.setText(bookName);
        summaryTextView.setText(detail);
    }
}