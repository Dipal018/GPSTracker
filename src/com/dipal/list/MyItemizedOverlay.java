package com.dipal.list;
    
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
        
    private ArrayList<OverlayItem> myOverlays ;
    private Context mContext;
    private static final int MINLATITUDE = (int)(+81 * 1E6);
	private static final int MAXLATITUDE = (int)(-81 * 1E6);
	private static final int MINLONGITUDE  = (int)(+181 * 1E6);
	private static final int MAXLONGITUDE  = (int)(-181 * 1E6);
	
	private int minLat;
	private int maxLat;
	private int minLng;
	private int maxLng;

    public MyItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        myOverlays = new ArrayList<OverlayItem>();
        populate();
        minLat=MINLATITUDE; maxLat=MAXLATITUDE;
        minLng=MINLONGITUDE; maxLng=MAXLONGITUDE;
    }
    
    public MyItemizedOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        myOverlays = new ArrayList<OverlayItem>();
        mContext=context;
        populate();
        minLat=MINLATITUDE; maxLat=MAXLATITUDE;
        minLng=MINLONGITUDE; maxLng=MAXLONGITUDE;
    }
        
    public void addOverlay(OverlayItem overlay){
        myOverlays.add(overlay);
        int latitude = overlay.getPoint().getLatitudeE6();
        int longitude = overlay.getPoint().getLongitudeE6();
 
        minLat = (minLat > latitude) ? latitude : minLat;
		maxLat = (maxLat < latitude) ? latitude : maxLat;		
		minLng = (minLng > longitude) ? longitude : minLng;
		maxLng = (maxLng < longitude) ? longitude : maxLng;
        
    }
    
    public void populateNow() {
    	populate();
    }
    
    public int getMinLatOfOverlay() {
    	return minLat;
    }
    
    public int getMaxLatOfOverlay() {
    	return maxLat;
    }
    
    public int getMinLngOfOverlay() {
    	return minLng;
    }
    
    public int getMaxLngOfOverlay() {
    	return maxLng;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return myOverlays.get(i);
    }
        
    // Removes overlay item i
    public void removeItem(int i){
        myOverlays.remove(i);
        populate();
    }
        
    // Handle tap events on overlay icons
    @Override
    protected boolean onTap(int i){
      
        
        GeoPoint  gpoint = myOverlays.get(i).getPoint();
        double lat = gpoint.getLatitudeE6()/1e6;
        double lon = gpoint.getLongitudeE6()/1e6;
        String toast = "Time: "+myOverlays.get(i).getTitle();
        //toast += "\nText: "+myOverlays.get(i).getSnippet();
        toast += 	"\nLatitude = "+lat+"\nLongitude = "+lon;
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        return(true);
    }

    // Returns present number of items in list
    @Override
    public int size() {
        return myOverlays.size();
    }
}