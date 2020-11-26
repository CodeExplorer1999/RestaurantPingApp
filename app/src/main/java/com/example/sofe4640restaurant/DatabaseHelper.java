package com.example.sofe4640restaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE = "restaurant_reminder";
    private static final String COL0 = "id";
    private static final String COL1 = "name";
    private static final String COL2 = "address";
    private static final String COL3 = "coordinate";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + " TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public boolean addData(Place currentPlace) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, currentPlace.getName());
        contentValues.put(COL2, currentPlace.getAddress());
        contentValues.put(COL3, String.valueOf(currentPlace.getLatLng()));

        long result = db.insert(TABLE, null, contentValues);

        if (result == -1) {
            return false;
        } else {
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
        db.delete(TABLE, COL1 + "=\"" + name +"\"", null);
    }

    /**
     * Returns number of entries in database
     */
    public int getNumberOfEntries() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "select * from " + TABLE;
        Cursor c = db.rawQuery(countQuery, null);
        count = c.getCount();

        return count;
    }

    public LatLng getCoordinates(int index) {
        LatLng location_LatLng = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE, null);
        c.moveToPosition(index);

        String s = c.getString(3).substring(10, c.getString(3).length() - 1);
        String[] latLng = s.split(",");
        location_LatLng = new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));

        return location_LatLng;
    }

    public String getAddress(int index) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE, null);
        c.move(index);
        String address = c.getString(2);

        return address;
    }
}