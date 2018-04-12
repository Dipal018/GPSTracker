package com.dipal.list;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class GPSDatabase {

	private static final String GPS_TABLE_NAME = "GPS";
	private static final String DEVICE_TABLE_NAME = "RegisteredDevices";
	private static final String SEQ_TABLE_NAME = "sqlite_sequence";
	private static final String JOIN_TABLE=GPS_TABLE_NAME+" JOIN "+DEVICE_TABLE_NAME
	+" ON ("+GPS_TABLE_NAME+".device_id = "+DEVICE_TABLE_NAME+"._id)";

	
	SQLiteDatabase db ;
	
	public GPSDatabase() {
		DatabaseHelper helper = new DatabaseHelper("GPS_DB", Environment.getExternalStorageDirectory().getAbsolutePath());
		this.db=helper.getWritableDatabase();
	}
	
	public String insertLocation(String lat,String lng,String date,String time, String imei, String phoneNumber)
	{
		ContentValues initialValues= new ContentValues();
		initialValues.put("lat",lat);
		initialValues.put("lng", lng);
		initialValues.put("date", date);
		initialValues.put("time",time);
		
		Cursor cur = db.query(true, DEVICE_TABLE_NAME, new String[] { "_id"}, "imei=? AND phoneNumber=?", new String[] { imei, phoneNumber}, null, null, null, null);
		cur.moveToFirst();
		if(! cur.isAfterLast()) {
			String devId = cur.getString(0);
			initialValues.put("device_id",devId);
			db.insert(GPS_TABLE_NAME,null,initialValues);
			return devId;
		}
		
		return null;
		
	}

	public void close() {
		db.close();
		
	}
	
	public String registerDevice(GPSDeviceInfo info) {
		ContentValues initialValues= new ContentValues();
		initialValues.put("imei",info.getDeviceIMEI());
		initialValues.put("name", info.getDeviceName());
		initialValues.put("phoneNumber", info.getDevicePhoneNumber());
		
			
		db.insert(DEVICE_TABLE_NAME,null,initialValues);
		String next="";
		Cursor cur = db.query(true, SEQ_TABLE_NAME, new String[] { "seq"}, "name = ?", new String[] { DEVICE_TABLE_NAME }, null, null, null, null);
		cur.moveToFirst();
		if(! cur.isAfterLast()) {
			next=cur.getString(0);
		}

		
		return next;
	}
	
	public ArrayList<GPSDeviceInfo> getDeviceList() {
		ArrayList<GPSDeviceInfo> devices = new ArrayList<GPSDeviceInfo>();
		Cursor cur = db.query(true, DEVICE_TABLE_NAME, new String[] { "_id", "name", "imei", "phoneNumber" }, null, null, null, null, null, null);
		cur.moveToFirst();
		while(! cur.isAfterLast()) {
			String devID = cur.getString(0);
			String devName = cur.getString(1);
			String imei = cur.getString(2);
			String phoneNumber = cur.getString(3);
			
			GPSDeviceInfo dev = new GPSDeviceInfo();
			dev.setDeviceID(devID);
			dev.setDeviceName(devName);
			dev.setDeviceIMEI(imei);
			dev.setDevicePhoneNumber(phoneNumber);
			devices.add(dev);	
			
			cur.moveToNext();
		}
		cur.close();
		return devices;
	}

	public void changeDeviceInfo(GPSDeviceInfo oldInfo, GPSDeviceInfo newInfo) {
		ContentValues updatedValues= new ContentValues();
		updatedValues.put("imei",newInfo.getDeviceIMEI());
		updatedValues.put("name", newInfo.getDeviceName());
		db.update(DEVICE_TABLE_NAME, updatedValues, "name=? AND imei=?", new String[] { oldInfo.getDeviceName(), oldInfo.getDeviceIMEI() } );
		
	}
	
	public void removeDevice(GPSDeviceInfo info) {
		/* delete device and GPS info of that device */
		db.delete(GPS_TABLE_NAME, "device_id=?", new String[] { info.getDeviceName() });
		db.delete(DEVICE_TABLE_NAME, "_id=?", new String[] {info.getDeviceID() });
	}
	
	public ArrayList<LocationInfo> getLocationPoints(String ofDevice, String onDate) {
		ArrayList<LocationInfo> locations = new ArrayList<LocationInfo>();
		Cursor cur = db.query(JOIN_TABLE, new String[] { DEVICE_TABLE_NAME+".name",
														 GPS_TABLE_NAME+".lat", 
														 GPS_TABLE_NAME+".lng",
														 GPS_TABLE_NAME+".date",
														 GPS_TABLE_NAME+".time",
														}
										, DEVICE_TABLE_NAME+".name=? AND "+GPS_TABLE_NAME+".date=?"
										, new String[] { ofDevice, onDate }, null, null, null);
		cur.moveToFirst();
		while(! cur.isAfterLast()) {
			LocationInfo loc = new LocationInfo();
			loc.setDeviceName(cur.getString(0));
			loc.setLat(cur.getString(1));
			loc.setLng(cur.getString(2));
			loc.setDate(cur.getString(3));
			loc.setTime(cur.getString(4));
			locations.add(loc);	
			cur.moveToNext();
		}
		cur.close();
		return locations;
	}
	
}
