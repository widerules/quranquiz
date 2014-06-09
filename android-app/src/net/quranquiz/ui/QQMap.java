/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.ui;

import net.quranquiz.R;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class QQMap extends FragmentActivity implements LocationListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    private LocationManager locationManager;
    private String provider;
    private LatLng cairo;
    private Marker meMarker;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setOnMarkerClickListener((OnMarkerClickListener) this);

        cairo = new LatLng(30.1,31.45);
        
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to 
        // go to the settings
        if (!enabledGPS) {
            Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); 
        criteria.setAltitudeRequired(false); 
        criteria.setBearingRequired(false); 
        criteria.setCostAllowed(true); 
        criteria.setPowerRequirement(Criteria.POWER_LOW); 
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            Toast.makeText(this, "Selected Provider " + provider,
                    Toast.LENGTH_SHORT).show();
            meMarker = map.addMarker(new MarkerOptions()
            .position(cairo)
            .title("That's Me")
            .snippet("a normal QuranQuiz Node!")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cairo,12.0f));
            
            onLocationChanged(location);
        } else {

            //do something
        }

    }

    /**
     * handle marker click event
     */    
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        if(marker.equals(meMarker)){
            Toast.makeText(this, "That's me!",Toast.LENGTH_SHORT).show();
            return true;
        }
            return false;           
    }
    
    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void onLocationChanged(Location location) {
        double lat =  location.getLatitude();
        double lng = location.getLongitude();
        Toast.makeText(this, "Location " + lat+","+lng,
                Toast.LENGTH_LONG).show();
        LatLng coordinate = new LatLng(lat, lng);

        meMarker.setPosition(coordinate);
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
        
    }

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}