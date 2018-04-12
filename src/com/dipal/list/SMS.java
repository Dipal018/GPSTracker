package com.dipal.list;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;


public class SMS extends BroadcastReceiver {
    /* package */ static final String ACTION =
            "android.provider.Telephony.SMS_RECEIVED";
    private static final String dateFormat="\\d+/\\d+/\\d+";
	private static final String timeFormat="\\d+:\\d+";
	private static final String doubleFormat="[+-]?[0-9.]+";
	private static final Pattern pattern = Pattern.compile("lat: ("+doubleFormat+")[ ]+long: ("+doubleFormat+")[ ]+speed: "+doubleFormat+"[ ]+("
			+dateFormat+")[ ]+("+timeFormat+")[ ]+bat:\\w[ ]+signal:\\w[ ]+imei:(\\d+)");

	@Override
	// source: http://www.devx.com/wireless/Article/39495/1954
	public void onReceive(Context context, Intent intent) {
		
		if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {// {
			return;
		}
		
		SmsMessage msgs[] = getMessagesFromIntent(intent);
		
		for (int i = 0; i < msgs.length; i++) {
			String message = msgs[i].getDisplayMessageBody();
			Log.i("GPS_SMS_Receiver",message);
			Log.i("GPS_SMS_Receiver",msgs[i].getOriginatingAddress());
			if (message != null && message.length() > 0) {
				Log.d("MessageListener", message);
				
				String lat="", lng="", speed="", date="", time="", imei="";
				
				Matcher matcher = pattern.matcher(message);
				
				boolean matches = matcher.matches();
				if(! matches)
					return;
				
				//matcher.find();
				
				Log.i("GPS_SMS_Receiver","format matched");
				lat=matcher.group(1);
				lng=matcher.group(2);
				date=matcher.group(3);
				time=matcher.group(4);
				imei=matcher.group(5);
				
				Log.i("GPS_SMS_Receiver",lat+" "+lng+" "+date+" "+time+" "+imei);
				
				GPSDatabase db = new GPSDatabase();
				
				db.insertLocation(lat, lng, date, time,imei, msgs[i].getOriginatingAddress());
				
				db.close();
			}
		}
		abortBroadcast();

	}
	
	private SmsMessage[] getMessagesFromIntent(Intent intent) {
		SmsMessage retMsgs[] = null;
		Bundle bdl = intent.getExtras();
		try {
			Object pdus[] = (Object[]) bdl.get("pdus");
			retMsgs = new SmsMessage[pdus.length];
			for (int n = 0; n < pdus.length; n++) {
				byte[] byteData = (byte[]) pdus[n];
				retMsgs[n] = SmsMessage.createFromPdu(byteData);
			}

		} catch (Exception e) {
			Log.e("GetMessages", "fail", e);
		}
		return retMsgs;
	}
}
