package com.example.android.inventorypart2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ToyContract {

    private ToyContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventorypart2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TOYS = "toys";

    public static final class ToyEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TOYS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOYS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOYS;

        public final static String TABLE_NAME = "toy";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "name";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";

        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }
}



