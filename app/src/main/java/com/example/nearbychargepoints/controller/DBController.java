package com.example.nearbychargepoints.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
public class DBController extends SQLiteOpenHelper {
    public static String tableName = "Locations";
    public static String colId = "Reference_ID";
    public static String colLon = "Longitude";
    public static String colLat = "Latitude";
    public static String colTow = "Town";
    public static String colCou = "County";
    public static String colPos = "Postcode";
    public static String colSta = "Status";
    public static String colConID = "Connector_ID";
    public static String colConty = "Connector_Type";


    // Users table
    public static String TABLE_USERS = "users";
    public static String USER_ID = "id";
    public static String USER_EMAIL = "email";
    public static String USER_PASSWORD = "password";
    ;

    public DBController(Context applicationcontext) {
        super(applicationcontext, "XLocations.db", null, 3); // creating DATABASE
        Toast.makeText(applicationcontext, "Database Created ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS " + tableName + "( " + colId + " TEXT, " + colLon +
                " TEXT, " + colLat + " TEXT, " + colTow + " TEXT, " + colCou + " TEXT, " + colPos +
                " TEXT, " + colSta + " TEXT, " + colConID + " TEXT, " + colConty + " TEXT) ";
// Toast.makeText(this,"Query Created "+query, Toast.LENGTH_SHORT).show();
        database.execSQL(query);


        // Create Users table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_EMAIL + " TEXT UNIQUE, " +
                USER_PASSWORD + " TEXT )";
        database.execSQL(createUsersTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + tableName;
        String createUserTable = "DROP TABLE IF EXISTS " + TABLE_USERS;
        database.execSQL(query);
        database.execSQL(createUserTable);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllProducts() {
        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + tableName;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
//Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));

                productList.add(map);
            } while (cursor.moveToNext());
        }
        return productList;
    }

    public long addUser(String email, String hashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, hashedPassword);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Method to get a user by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + USER_EMAIL + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        return cursor;
    }
}