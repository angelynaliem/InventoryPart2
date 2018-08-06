package com.example.android.inventorypart2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventorypart2.data.ToyContract.ToyEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_TOY_LOADER = 0;

    private Uri mCurrentToyUri;

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierNameEditText;

    private EditText mSupplierContactNumberEditText;

    private Button increaseQuantityButton;
    private Button decreaseQuantityButton;
    private Button restockButton;
    private Button deleteToyButton;

    private boolean mToyHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mToyHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentToyUri = intent.getData();

        increaseQuantityButton = findViewById(R.id.increase_quantity);
        decreaseQuantityButton = findViewById(R.id.decrease_quantity);
        restockButton = findViewById(R.id.restock_button);
        deleteToyButton = findViewById(R.id.delete_toy_button);

        if (mCurrentToyUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_toy));

            invalidateOptionsMenu();

        } else {

            setTitle(getString(R.string.editor_activity_title_edit_toy));

            getLoaderManager().initLoader(EXISTING_TOY_LOADER, null, this);

        }

        mNameEditText = findViewById(R.id.edit_toy_name);
        mPriceEditText = findViewById(R.id.edit_toy_price);
        mQuantityEditText = findViewById(R.id.edit_toy_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_toy_supplier_name);
        mSupplierContactNumberEditText = findViewById(R.id.edit_toy_supplier_contact_number);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierContactNumberEditText.setOnTouchListener(mTouchListener);

    }


    private void saveToy() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierContactNumberString = mSupplierContactNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.name_required),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.price_required),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.supplier_name_required),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(supplierContactNumberString)) {
            Toast.makeText(this, getString(R.string.supplier_contact_number_required),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Long quantity = Long.parseLong(quantityString);
        Long supplierContactNumber = Long.parseLong(supplierContactNumberString);

        if (mCurrentToyUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierContactNumberString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ToyEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ToyEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ToyEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        values.put(ToyEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT_NUMBER, supplierContactNumber);

        if (mCurrentToyUri == null) {
            Uri newUri = getContentResolver().insert(ToyEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_toy_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_toy_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentToyUri, values, null, null);

            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_toy_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_toy_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:

                saveToy();
                finish();
                return true;

            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                    return true;

            case android.R.id.home:
                if (!mToyHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;

                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mToyHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ToyEntry._ID,
                ToyEntry.COLUMN_PRODUCT_NAME,
                ToyEntry.COLUMN_PRODUCT_PRICE,
                ToyEntry.COLUMN_PRODUCT_QUANTITY,
                ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ToyEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT_NUMBER};

        return new CursorLoader(this,
                mCurrentToyUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierContactNumberColumnIndex = cursor.getColumnIndex(ToyEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierContactNumber = cursor.getString(supplierContactNumberColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierContactNumberEditText.setText(supplierContactNumber);

            increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityString = mQuantityEditText.getText().toString();
                    String stringValue = quantityString.matches("") ? "0" : quantityString;
                    int quantity = Integer.parseInt(stringValue);
                    updateQuantity(quantity, false);
                }
            });

            decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityString = mQuantityEditText.getText().toString();
                    if (quantityString.matches("")) {
                        mQuantityEditText.setText("0");
                    }
                    String stringValue = quantityString.matches("") ? "0" : quantityString;
                    int quantity = Integer.parseInt(stringValue);
                    updateQuantity(quantity, true);
                }
            });

            deleteToyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            restockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supplierContactNumber, null));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierContactNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteToy();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteToy() {

        if (mCurrentToyUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentToyUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_toy_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_toy_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void updateQuantity(int quantity, boolean increase) {

        if (increase) {
            quantity++;
        } else {
            quantity--;
        }

        if (mCurrentToyUri != null) {
            if (quantity >= 0) {
                ContentValues values = new ContentValues();
                values.put(ToyEntry.COLUMN_PRODUCT_QUANTITY, quantity);

                int rowsAffected = getContentResolver().update(mCurrentToyUri, values, null, null);

                if (rowsAffected == 0) {

                    Toast.makeText(this, "Error increasing quantity.",
                            Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(this, "Quantity is increased.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "You have 0 inventory for this toy.", Toast.LENGTH_LONG).show();
            }
        }
    }
}