package com.dipal.list;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ListView extends ListActivity {
	
	private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<GPSDeviceInfo> m_devices = null;
    private GPSDeviceInfoAdapter m_adapter;
    private Runnable viewDevices;
    private Context context;
    
    private final static int BIGINT = 999999;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        m_devices = new ArrayList<GPSDeviceInfo>();
        this.m_adapter = new GPSDeviceInfoAdapter(this, R.layout.row, m_devices);
                setListAdapter(this.m_adapter);
                
        registerForContextMenu(getListView());
        context = getApplicationContext();
        
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("day", mDay);
        editor.putInt("month", mMonth);
        editor.putInt("year", mYear);
        editor.commit();
        
        
        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                    long id) {
                // Do your stuff here
            	GPSDeviceInfo deviceInfo = (GPSDeviceInfo)av.getItemAtPosition(position);
        		Intent i = new Intent(context, ShowTheMap.class);
        		Bundle b = new Bundle();
        		b.putSerializable("deviceInfo", deviceInfo);
        		i.putExtras(b);
        		startActivity(i);
        		Log.i("LIST_VIEW", "item clicked");
            //	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			//	alertDialog.setMessage("OKKKKKKKK");
            }
        });
                
        viewDevices = new Runnable(){
            @Override
            public void run() {
                getDevices();
            }
        };
        Thread thread =  new Thread(null, viewDevices, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(ListView.this,    
              "Please wait...", "Retrieving data ...", true);
    }
    
	private void getDevices(){
        try{
        	GPSDatabase db = new GPSDatabase();
        	
            m_devices = db.getDeviceList();
            db.close();
          } catch (Exception e) { 
          }
          runOnUiThread(returnRes);
      }
	
	private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_devices != null && m_devices.size() > 0){
            	for(int i=0; i<m_devices.size() ; i++) {
            		m_adapter.add(m_devices.get(i));
            	}
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
      };

    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
    		super.onCreateContextMenu(menu, v, menuInfo);
    	      MenuInflater inflater = getMenuInflater();
    	      menu.setHeaderTitle("Options");
    	      inflater.inflate(R.menu.context_menu, menu);
    		
    }
    
    @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	  super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_screen_options_menu,(Menu) menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	switch (menuItem.getItemId()) {
    		case R.id.Add:
    			addNewDevice();
    			return true;
    		case R.id.About:
    			showAbout();
    			return true;
    		default:
    			return super.onOptionsItemSelected(menuItem);
        }
    }
    
    private void showAbout() {
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setMessage("Developed By : Dipal Patel and Tanvi Hariyani");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) 
			{
					dialog.dismiss();			        		
			}
			});

		alertDialog.show();

	}

	

	private void addNewDevice() {
		Intent i = new Intent(context, AddDevice.class);
		startActivityForResult(i, BIGINT);
		
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
      case R.id.edit:
    	  Intent i = new Intent(context, EditGPSDeviceInfo.class);
    	  Bundle b = new Bundle();
    	  GPSDeviceInfo orig = m_adapter.getItem((int) info.position);
    	  b.putSerializable("Original", orig);
    	  i.putExtras(b);
      		//m_adapter.remove(orig);
    	  startActivityForResult(i,info.position);
        return true;
      case R.id.delete:
    	  long pos = info.position;
    	  GPSDeviceInfo gpsInfo = m_adapter.getItem((int)pos);
    	  Log.i("GPS", "deleting  : "+gpsInfo.getDeviceName());
    	  GPSDatabase db = new GPSDatabase();
    	  db.removeDevice(gpsInfo);
    	  db.close();
    	  
    	  m_adapter.remove(m_adapter.getItem((int)pos));
    	  m_adapter.notifyDataSetChanged();
        return true;
      default:
        return super.onContextItemSelected(item);
      }
    }
    
          
      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            	if(requestCode!=BIGINT && resultCode==RESULT_OK) {	//edit device
            		GPSDeviceInfo old = m_adapter.getItem(requestCode);
                	//old.setOrderName(order.getOrderName());
                	m_adapter.remove(old);
                	
                	Bundle b = data.getExtras();
                	GPSDeviceInfo newer = (GPSDeviceInfo) b.getSerializable("Changed");
                	
                	m_adapter.insert(newer, requestCode);
                	m_adapter.notifyDataSetChanged();
                	GPSDatabase db = new GPSDatabase();
                	db.changeDeviceInfo(old,newer);
                	db.close();
                	

                	//m_adapter.notifyDataSetInvalidated();
                	
            	}
            	else if(requestCode==BIGINT && resultCode==RESULT_OK) {	//add device
            		Bundle b = data.getExtras();
            		GPSDeviceInfo info = (GPSDeviceInfo) b.getSerializable("New");
            		
            		GPSDatabase db = new GPSDatabase();
            		String id=db.registerDevice(info);
            		db.close();
            		
            		info.setDeviceID(id);
            		Log.i("GPS", "Added dev id : "+info.getDeviceID());
            		
            		m_adapter.insert(info , m_adapter.getCount());
            		m_adapter.notifyDataSetChanged();
            	}
      }
}