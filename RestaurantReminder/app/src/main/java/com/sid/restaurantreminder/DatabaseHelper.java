package com.sid.restaurantreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE = "restaurant_reminder";
    private static final String COL0 = "id";
    private static final String COL1 = "name";
    private static final String COL2 = "address";

    public DatabaseHelper(Context context) {
        super(context, TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + " TEXT, " + COL2 + "TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public boolean addData(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, data);

        long result = db.insert(TABLE, null, contentValues);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE, null);
        return data;
    }

    public void delItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + COL1 + "= '" + name + "'";
        db.execSQL(query);
    }
}
