package com.dipal.list;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class ShowTheMap extends MapActivity {
    private MapController mapControl;
    private ProgressDialog m_ProgressDialog = null; 
    private GeoPoint gp;
    private MapView mapView;

    private List<Overlay> mapOverlays;
    private MyItemizedOverlay itemizedOverlay1;
    GPSDeviceInfo deviceInfo;
    
	private Drawable drawable1;
	private Runnable configureOverlay;
	
	private int mYear;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 0;
	
    // 	the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
    			@Override
                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("day", mDay);
                    editor.putInt("month", mMonth);
                    editor.putInt("year", mYear);
                    editor.commit();
                    Thread thread =  new Thread(null, configureOverlay, "MagentoBackground");
                    thread.start();
                }
            };
    
   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SHOW THE MAP","Showing map");
        //requestWindowFeature(Window.FEATURE_NO_TITLE);  // Suppress title bar for more space
        setContentView(R.layout.showthemap);
        
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mDay = preferences.getInt("day", mDay);
        mMonth = preferences.getInt("month", mMonth);
        mYear = preferences.getInt("year", mYear);

        Intent current = getIntent();
        Bundle b = current.getExtras();
        deviceInfo = (GPSDeviceInfo) b.getSerializable("deviceInfo");
        setTitle(deviceInfo.getDeviceName());
        
     // Add map controller with zoom controls
        mapView = (MapView) findViewById(R.id.mv);
        mapView.setSatellite(true);
        mapView.setTraffic(false);
        mapView.setBuiltInZoomControls(true);   // Set android:clickable=true in main.xml 
        mapControl = mapView.getController();

        
        m_ProgressDialog = ProgressDialog.show(ShowTheMap.this,    
                "Please wait...", "Retrieving data ...", true);
        
        configureOverlay = new Runnable(){
            @Override
            public void run() {
                setOverlay1();
            }
        };
        Thread thread =  new Thread(null, configureOverlay, "MagentoBackground");
        thread.start();
    }
    

    
    public void setOverlay1(){
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mDay = preferences.getInt("day", mDay);
        mMonth = preferences.getInt("month", mMonth);
        mYear = preferences.getInt("year", mYear);
        
    	final String date = String.format("%02d", mDay)+"/"+String.format("%02d", mMonth+1)+"/"+String.format("%02d", mYear%100);
        GPSDatabase db = new GPSDatabase();
        ArrayList<LocationInfo> points = db.getLocationPoints(deviceInfo.getDeviceName(), date);
        db.close();
        
        if(points.size()==0) {
        	m_ProgressDialog.dismiss();
        	//int minLat=37062500, maxLat=37598824, minLng=-95677068, maxLng=86572266;
        	mapControl.zoomToSpan(11020420,21643066);
        	mapControl.animateTo(new GeoPoint(21820708,79145508));
        	
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mapView.invalidate();
					Toast.makeText(getApplicationContext(), "No Location Records found on "+date, Toast.LENGTH_LONG).show();
				}
			});
        	
        	return;
        }
        
        // Create itemizedOverlay2 if it doesn't exist and display all three items
        mapOverlays = mapView.getOverlays();	
        drawable1 = this.getResources().getDrawable(R.drawable.marker); 
        itemizedOverlay1 = new MyItemizedOverlay(drawable1,getApplicationContext()); 
        // Display all three items at once
        for(int i=0; i<points.size(); i++){
        	LocationInfo locInfo = points.get(i);
        	int latE6 = (int) (Double.parseDouble(locInfo.getLat())*1e6);
        	int lngE6 = (int) (Double.parseDouble(locInfo.getLng())*1e6);
            itemizedOverlay1.addOverlay(new OverlayItem(new GeoPoint(latE6, lngE6), locInfo.getDate()+" "+locInfo.getTime(), null));
        }
        itemizedOverlay1.populateNow();
        
        mapOverlays.add(itemizedOverlay1);
        	
        	mapControl.zoomToSpan(itemizedOverlay1.getMaxLatOfOverlay()-itemizedOverlay1.getMinLatOfOverlay()
 				, itemizedOverlay1.getMaxLngOfOverlay()-itemizedOverlay1.getMinLngOfOverlay());
        	mapControl.animateTo(new GeoPoint(
 				(itemizedOverlay1.getMaxLatOfOverlay()+itemizedOverlay1.getMinLatOfOverlay())/2,
 				(itemizedOverlay1.getMaxLngOfOverlay()+itemizedOverlay1.getMinLngOfOverlay())/2
				));
        	if (points.size()==1) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mapControl.zoomOut();
						mapControl.zoomOut();
						
					}
				});
			}
        	
        	Log.i("SHOW THE MAP","map shown");
        	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		m_ProgressDialog.dismiss();
    	}
    };
        
    
    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_S){
            mapView.setSatellite(!mapView.isSatellite());
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_T){
            mapView.setTraffic(!mapView.isTraffic());
            mapControl.animateTo(gp);  // To ensure change displays immediately
        }
            return(super.onKeyDown(keyCode, e));
    }
                        
    // Required method since class extends MapActivity
    @Override
    protected boolean isRouteDisplayed() {
            return false;  // Don't display a route
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
  	  super.onCreateOptionsMenu(menu);
          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.map_view_options_menu,(Menu) menu);
          return true;
      }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	switch (menuItem.getItemId()) {
    		case R.id.ondate:
    			showDialog(DATE_DIALOG_ID);
    			return true;
    		case R.id.refreshmap:
    			Thread thread =  new Thread(null, configureOverlay, "MagentoBackground");
    	        thread.start();
    			return true;
    		default:
    			return super.onOptionsItemSelected(menuItem);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }
    
    
}