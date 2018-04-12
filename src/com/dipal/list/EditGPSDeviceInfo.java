package com.dipal.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditGPSDeviceInfo extends Activity implements OnClickListener {
	
	GPSDeviceInfo deviceInfo;
	EditText editor;
	Activity current;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editorderlayout);
        current = EditGPSDeviceInfo.this;
        
        Intent i = getIntent();
        Bundle b = i.getExtras();
        deviceInfo = (GPSDeviceInfo) b.getSerializable("Original");
        
        editor = (EditText) findViewById(R.id.EditText);
        editor.setText(deviceInfo.getDeviceName());
        
        Button save = (Button) findViewById(R.id.SaveButton);
        Button cancel = (Button) findViewById(R.id.CancelButton);
        
        save.setOnClickListener( this );
        cancel.setOnClickListener(new Button.OnClickListener () {
        	@Override
        	public void onClick(View view) {
        		setResult(RESULT_CANCELED, null);
        		finish();
        	}
        });
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		deviceInfo.setDeviceName(editor.getText().toString());
		Intent resultIntent = getIntent();
        Bundle b = resultIntent.getExtras();
        b.putSerializable("Changed", deviceInfo);
    	resultIntent.putExtras(b);
    	setResult(RESULT_OK, resultIntent); 
		current.finish();
	}
}
