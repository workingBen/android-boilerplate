package com.bpellow.android.boilerplate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "android_boilerplate_database";
    protected static final int DATABASE_VERSION = 1;
    protected static final String ITEM_TABLE_CREATE = "CREATE TABLE items (_id integer, "
		+ "content text not null, favorited boolean, favorited_at integer);";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ITEM_TABLE_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) { 
    	Log.w(DBOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion);
    }
    
}