package com.palibre.mysqlsync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.palibre.mysqlsync.DbContract.DATABASE_NAME;
import static com.palibre.mysqlsync.DbContract.NAME;
import static com.palibre.mysqlsync.DbContract.SYNC_STATUS;
import static com.palibre.mysqlsync.DbContract.TABLE_NAME;


public class DBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +
                    SYNC_STATUS + " INTEGER);";
    private static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    void saveToLocalDatabase(String name, int syncStatus, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(SYNC_STATUS, syncStatus);
        db.insert(TABLE_NAME, null, cv);
    }

    Cursor readFromLocalDatabase(SQLiteDatabase db) {
        String[] proj = {NAME, SYNC_STATUS};
        return db.query(TABLE_NAME, proj, null, null, null, null, null);

    }

    public void updateLocalDatabase(String name, int syncStatus, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(SYNC_STATUS, syncStatus);
        String selection = NAME + " like ?";
        String selArgs[] = {name};
        db.update(TABLE_NAME, cv, selection, selArgs);
    }
}
