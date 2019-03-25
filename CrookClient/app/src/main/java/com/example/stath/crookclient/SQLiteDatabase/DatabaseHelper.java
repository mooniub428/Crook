package com.example.stath.crookclient.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "Crook.db";
    public static final String TABLE_NAME = "cart_table";
    public static final String COL_CARTITEM_ID = "ID";
    public static final String COL_PRODUCT_ID = "PRODUCT_ID";
    public static final String COL_QUANTITY = "QUANTITY";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("+
                COL_CARTITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_ID + " INTEGER UNIQUE, " +
                COL_QUANTITY + " INTEGER)";
        db.execSQL(query);
        Log.d(TAG, "onCreate: Called");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public boolean insertData(int product_id, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PRODUCT_ID, product_id);
        contentValues.put(COL_QUANTITY, quantity);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getCartItemUsingProductId(int product_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE PRODUCT_ID=" + product_id;
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public void incQuantityOfProduct(int product_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE PRODUCT_ID=" + product_id;
        Cursor res = db.rawQuery(query, null);

        res.moveToFirst();
        int quantity = res.getInt(2);
        quantity += 1;

        res.close();    //NOTE: this HAS to be called cause if a function is called using the
                        // same instance, it might cause some problems

        if(updateData(product_id, quantity)){
            Log.d(TAG, "incQuantityOfProduct: Update of product " +
                    product_id + " was successful");
        } else {
            Log.d(TAG, "incQuantityOfProduct: Update of product " +
                    product_id + " failed");
        }
    }

    public int decQuantityOfProduct(int product_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE PRODUCT_ID=" + product_id;
        Cursor res = db.rawQuery(query, null);

        res.moveToFirst();
        int quantity = res.getInt(2);
        quantity -= 1;

        res.close();    //NOTE: this HAS to be called

        if(quantity > 0){
            if(updateData(product_id, quantity)){
                Log.d(TAG, "decQuantityOfProduct: Update of product " +
                        product_id + " was successful");
            } else {
                Log.d(TAG, "decQuantityOfProduct: Update of product " +
                        product_id + " failed");
            }
        }
        else {
            if(deleteData(product_id))
                Log.d(TAG, "decQuantityOfProduct: Deletion of product "
                        + product_id + " was successfull");
            else
                Log.d(TAG, "decQuantityOfProduct: Something went wrong with deleting product"+
                        product_id);
        }

        return quantity;
    }

    public Cursor getCart(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public boolean updateData(int product_id, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PRODUCT_ID, product_id);
        contentValues.put(COL_QUANTITY, quantity);
        int rows_affected = db.update(TABLE_NAME, contentValues, "PRODUCT_ID = ?", new String[] {String.valueOf(product_id)});

        if(rows_affected == 1)
            return true;

        return false;
    }

    public boolean deleteData(int product_id){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows_affected = db.delete(TABLE_NAME, "PRODUCT_ID = ?", new String[]{String.valueOf(product_id)});

        if(rows_affected == 1){
            return true;
        }

        return false;
    }
}
