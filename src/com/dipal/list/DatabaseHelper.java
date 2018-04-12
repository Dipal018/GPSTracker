package com.dipal.list;


import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DatabaseHelper
{
    private static final String TAG		     = "DatabaseHelper";

    private String  dbPath;
    private String  dbName;
    public static final String  GPS_TABLE	    = "GPS";
    public static final String  DEVICE_TABLE    = "RegisteredDevices";

    private static final String GPS_TABLE_CREATE     = "create table "+GPS_TABLE+" (_id integer primary key autoincrement, "
	+ "lat text not null, lng text not null, date date not null, time text not null, device_id integer,"
	+ " FOREIGN KEY(device_id) REFERENCES "+DEVICE_TABLE+"(_id));";
    
    private static final String DEVICE_TABLE_CREATE     = "create table "+DEVICE_TABLE+" (_id integer primary key autoincrement, "
	+ "name text not null, imei text not null, phoneNumber text not null);";
    
    private SQLiteDatabase      database;

    public DatabaseHelper(String Name, String Path)
    {	
    	dbPath=Path; dbName=Name;
    	try {
            database = SQLiteDatabase.openDatabase(dbPath + File.separator + dbName, null, SQLiteDatabase.OPEN_READWRITE);
    	}
    	catch (SQLiteException ex) {
    		Log.e(TAG, "Creating DB -- " + ex.getMessage(), ex);
    		// error means tables does not exits
    		database = SQLiteDatabase.openOrCreateDatabase(dbPath + File.separator + dbName, null);
    		createTables();
    	}
    	finally {
    		if(database!=null)
    			database.close();
    	}
    }

    private void createTables() {
    	database.execSQL(GPS_TABLE_CREATE);
    	database.execSQL(DEVICE_TABLE_CREATE);
    }

    public void close()
    {
    	if(database!=null)
			database.close();
    }

    public SQLiteDatabase getReadableDatabase()
    {
    	database = SQLiteDatabase.openDatabase(dbPath + File.separator + dbName, null, SQLiteDatabase.OPEN_READONLY);
    	return database;
    }

    public SQLiteDatabase getWritableDatabase()
    {
    	database = SQLiteDatabase.openDatabase(dbPath + File.separator + dbName, null, SQLiteDatabase.OPEN_READWRITE);
    	return database;
    }

}
