package com.example.android.inventorypart2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventorypart2.data.ToyContract.ToyEntry;

public class ToyProvider extends ContentProvider {

    public static final String LOG_TAG = ToyProvider.class.getSimpleName();

    private static final int TOYS = 100;

    private static final int TOY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ToyContract.CONTENT_AUTHORITY, ToyContract.PATH_TOYS, TOYS);

        sUriMatcher.addURI(ToyContract.CONTENT_AUTHORITY, ToyContract.PATH_TOYS + "/#", TOY_ID);

    }


    private ToyDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ToyDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TOYS:
                cursor = database.query(ToyEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TOY_ID:
                selection = ToyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ToyEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOYS:
                return insertToy(uri, contentValues);
            default:
                throw new IllegalArgumentException("Not supported " + uri);
        }
    }

    private Uri insertToy(Uri uri, ContentValues values) {
        String nameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_NAME);
        if (nameValue == null) {
            throw new IllegalArgumentException("Please state toy's name");
        }

        double priceValue = values.getAsDouble(ToyEntry.COLUMN_PRODUCT_PRICE);
        if (priceValue < 0) {
            throw new IllegalArgumentException("Please state price");
        }

        int quantityValue = values.getAsInteger(ToyEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantityValue < 0) {
            throw new IllegalArgumentException("Please state quantity in stock");
        }

        String supplierNameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierNameValue == null) {
            throw new IllegalArgumentException("Please state supplier's name");
        }

        String supplierPhoneNumberValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumberValue == null) {
            throw new IllegalArgumentException("Please state supplier's contact no.");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ToyEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Row insertion failed " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOYS:
                return updateToy(uri, contentValues, selection, selectionArgs);
            case TOY_ID:
                selection = ToyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateToy(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Failed to update " + uri);
        }
    }

    private int updateToy(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_NAME)) {
            String nameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_NAME);
            if (nameValue == null) {
                throw new IllegalArgumentException("Please state toy's name");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_PRICE)) {
            double priceValue = values.getAsDouble(ToyEntry.COLUMN_PRODUCT_PRICE);
            if (priceValue < 0) {
                throw new IllegalArgumentException("Please state price");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_QUANTITY)) {
            double quantityValue = values.getAsDouble(ToyEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantityValue < 0) {
                throw new IllegalArgumentException("Please state quantity");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierNameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierNameValue == null) {
                throw new IllegalArgumentException("Please state supplier's name");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumberValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumberValue == null) {
                throw new IllegalArgumentException("Please state supplier's contact no.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ToyEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOYS:
                rowsDeleted = database.delete(ToyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOY_ID:
                selection = ToyEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ToyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed to delete " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOYS:
                return ToyEntry.CONTENT_LIST_TYPE;
            case TOY_ID:
                return ToyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown " + uri + " with " + match);
        }
    }
}
