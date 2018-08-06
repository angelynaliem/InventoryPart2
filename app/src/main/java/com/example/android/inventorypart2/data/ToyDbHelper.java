package com.example.android.inventorypart2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventorypart2.data.ToyContract.ToyEntry;

public class ToyDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "toys.db";

    private static final int DATABASE_VERSION = 1;

    public ToyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TOYS_TABLE = " CREATE TABLE " + ToyEntry.TABLE_NAME + " ( "
                + ToyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToyEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ToyEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + ToyEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ToyEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT_NUMBER + " LONG NOT NULL); ";

        db.execSQL(SQL_CREATE_TOYS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
