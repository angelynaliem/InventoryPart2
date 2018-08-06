package com.example.android.inventorypart2;

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
import android.widget.Toast;

import com.example.android.inventorypart2.data.ToyContract.ToyEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TOY_LOADER = 0;

    ToyCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView toyListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        toyListView.setEmptyView(emptyView);

        mCursorAdapter = new ToyCursorAdapter(this, null);
        toyListView.setAdapter(mCursorAdapter);

        toyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentToyUri = ContentUris.withAppendedId(ToyEntry.CONTENT_URI, id);

                intent.setData(currentToyUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(TOY_LOADER, null, this);
    }

    private void insertToy() {

        ContentValues values = new ContentValues();
        values.put(ToyEntry.COLUMN_PRODUCT_NAME, "Toy A");
        values.put(ToyEntry.COLUMN_PRODUCT_PRICE, 19.99);
        values.put(ToyEntry.COLUMN_PRODUCT_QUANTITY, 5);
        values.put(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Supplier A");
        values.put(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "1234567890");

        Uri newUri = getContentResolver().insert(ToyEntry.CONTENT_URI, values);
    }

    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(ToyEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from toys database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertToy();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ToyEntry._ID,
                ToyEntry.COLUMN_PRODUCT_NAME,
                ToyEntry.COLUMN_PRODUCT_PRICE,
                ToyEntry.COLUMN_PRODUCT_QUANTITY,
                ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                ToyEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public void doSale(int toyID, int quantity) {
        quantity--;

        if (quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(ToyEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            Uri updateUri = ContentUris.withAppendedId(ToyEntry.CONTENT_URI, toyID);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "This toy has been sold.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "This toy is no longer available.", Toast.LENGTH_LONG).show();
        }
    }
}