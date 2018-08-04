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
        sUriMatcher.addURI(ToyContract.CONTENT_AUTHORITY, ToyContract.PATH_TOY, TOYS);

        sUriMatcher.addURI(ToyContract.CONTENT_AUTHORITY, ToyContract.PATH_TOY + "/#", TOY_ID);

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
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
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
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        }

       private Uri insertToy(Uri uri, ContentValues values) {
           String nameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_NAME);
           if (nameValue == null) {
               throw new IllegalArgumentException("Toy requires a name.");
           }

           double priceValue = values.getAsDouble(ToyEntry.COLUMN_PRODUCT_PRICE);
           if (priceValue < 0) {
               throw new IllegalArgumentException("Toy requires a valid price.");
           }

           int quantityValue = values.getAsInteger(ToyEntry.COLUMN_PRODUCT_QUANTITY);
           if (quantityValue < 0) {
               throw new IllegalArgumentException("Toy requires a valid quantity.");
           }

           String supplierNameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
           if (supplierNameValue == null) {
               throw new IllegalArgumentException("Toy requires a supplier's name.");
           }

           String supplierPhoneNumberValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
           if (supplierPhoneNumberValue == null) {
               throw new IllegalArgumentException("Toy requires a supplier's phone number.");
           }

           SQLiteDatabase database = mDbHelper.getWritableDatabase();

           long id = database.insert(ToyEntry.TABLE_NAME, null, values);

           if (id == -1) {
               Log.e(LOG_TAG, "Failed to insert row for " + uri);
               return null;
           }

           getContext().getContentResolver().notifyChange(uri, null);

           return ContentUris.withAppendedId(uri, id);
       }

       @Override
       public int update (Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOYS:
                return updateToy(uri, contentValues, selection, selectionArgs);
            case TOY_ID:
                selection = ToyEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateToy(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
        }
       }

       private int updateToy(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            if (values.containsKey(ToyEntry.COLUMN_PRODUCT_NAME)) {
                String nameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_NAME);
                if (nameValue == null) {
                    throw new IllegalArgumentException("Toy requires a name");
                }
            }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_PRICE)) {
            double priceValue = values.getAsDouble(ToyEntry.COLUMN_PRODUCT_PRICE);
            if (priceValue < 0) {
                throw new IllegalArgumentException("Toy requires valid price.");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_QUANTITY)) {
            double quantityValue = values.getAsDouble(ToyEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantityValue < 0) {
                throw new IllegalArgumentException("Toy requires valid quantity.");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierNameValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierNameValue == null) {
                throw new IllegalArgumentException("Toy requires a supplier name.");
            }
        }

        if (values.containsKey(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumberValue = values.getAsString(ToyEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumberValue == null) {
                throw new IllegalArgumentException("Toy requires a supplier phone number.");
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
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
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
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
