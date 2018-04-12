package com.dipal.list;

//import Database.application.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddDevice extends Activity {
	Button save;
	Button cancel;
	EditText name, imei, phoneNo;
	GPSDatabase db;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddevice);
        
        save = (Button) findViewById(R.id.SaveButton);
        cancel = (Button) findViewById(R.id.CancelButton);
        
        name = (EditText) findViewById(R.id.adddevname);
        imei = (EditText) findViewById(R.id.addimeino);
        phoneNo = (EditText) findViewById(R.id.addphoneno);
        
        imei.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNo.setInputType(InputType.TYPE_CLASS_PHONE);
 
        save.setOnClickListener(new Button.OnClickListener () {
        	@Override
        	public void onClick(View view) {
        		GPSDeviceInfo devInfo = new GPSDeviceInfo();
        		devInfo.setDeviceName(name.getText().toString());
        		devInfo.setDeviceIMEI(imei.getText().toString());
        		devInfo.setDevicePhoneNumber(phoneNo.getText().toString());
        		
        		Intent resultIntent = getIntent();
                Bundle b = new Bundle();
                b.putSerializable("New", devInfo);
            	resultIntent.putExtras(b);
            	setResult(RESULT_OK, resultIntent); 
        		finish();
        	}
        });
        cancel.setOnClickListener(new Button.OnClickListener () {
        	@Override
        	public void onClick(View view) {
        		setResult(RESULT_CANCELED, null);
        		finish();
        	}
        });
        
    }
    
}
