package com.example.android.inventorypart2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventorypart2.data.ToyContract.ToyEntry;

public class ToyCursorAdapter extends CursorAdapter {

    public ToyCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        final int idColumnIndex = cursor.getColumnIndex(ToyEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_QUANTITY);

        String toyName = cursor.getString(nameColumnIndex);
        String toyPrice = cursor.getString(priceColumnIndex);
        final int toyQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(toyName);
        priceTextView.setText("$" + toyPrice);
        quantityTextView.setText(toyQuantity + " pcs");

        final int toyID = cursor.getInt(idColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);

        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogActivity Activity = (CatalogActivity) context;
                Activity.doSale(toyID, quantity);
            }
        });
    }

}

