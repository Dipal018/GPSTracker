package com.dipal.list;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GPSDeviceInfoAdapter extends ArrayAdapter<GPSDeviceInfo> {

	private ArrayList<GPSDeviceInfo> items;

    public GPSDeviceInfoAdapter(Context context, int textViewResourceId, ArrayList<GPSDeviceInfo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            GPSDeviceInfo o = items.get(position);
            if (o != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText("Name: "+o.getDeviceName());                            }
                    if(bt != null){
                          bt.setText("IMEI: "+ o.getDeviceIMEI());
                    }
            }
            return v;
    }
    
}
