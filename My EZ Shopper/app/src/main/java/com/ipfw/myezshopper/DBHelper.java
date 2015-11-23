package com.ipfw.myezshopper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int USER_DATABASE_VERSION = 1;
    private static final String USER_DATABASE_NAME = "users.db";
    private static final String USER_TABLE_NAME = "users";
    private static final String USER_COLUMN_ID = "id";
    private static final String USER_COLUMN_NAME = "name";
    private static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_COLUMN_EMAIL = "email";
    SQLiteDatabase db;
    private String query;
    private ContentValues values;
    private Cursor cursor;

    private static final String USER_TABLE_CREATE = "create table " + USER_TABLE_NAME + "(" +
            USER_COLUMN_ID + " integer primary key not null, " +
            USER_COLUMN_NAME + " text not null, " +
            USER_COLUMN_EMAIL + " text not null, " +
            USER_COLUMN_PASSWORD + " text not null);";

    public DBHelper(Context context){
        super(context, USER_DATABASE_NAME, null, USER_DATABASE_VERSION);
    }

    public void insertUser(User u){

        openDatabaseWriteAccess();

        query = "SELECT * FROM " + USER_TABLE_NAME;
        cursor = db.rawQuery(query,null);

        int count = cursor.getCount();
        values.put(USER_COLUMN_ID, count);
        values.put(USER_COLUMN_NAME, u.getName());
        values.put(USER_COLUMN_EMAIL, u.getEmail());
        values.put(USER_COLUMN_PASSWORD, u.getPassword());
        db.insert(USER_TABLE_NAME, null, values);
        db.close();
    }

    public String searchPass(String email){
        openDatabaseReadAccess();

        query = "SELECT " + USER_COLUMN_EMAIL + ", " + USER_COLUMN_PASSWORD + " FROM " + USER_TABLE_NAME;
        cursor = db.rawQuery(query, null);

        String a, b;
        b = "not found";
        if (cursor.moveToFirst()){
            do{
                a = cursor.getString(0);

                if (a.equals(email)){
                    b = cursor.getString(1);
                    break;
                }
            }while(cursor.moveToNext());
        }

        return b;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + USER_TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    private void openDatabaseWriteAccess(){
        db = this.getWritableDatabase();
        values = new ContentValues();
    }

    private void openDatabaseReadAccess(){
        db = this.getReadableDatabase();
    }

}
