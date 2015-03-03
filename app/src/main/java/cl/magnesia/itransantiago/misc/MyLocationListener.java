package cl.magnesia.itransantiago.misc;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ribarra on 2/19/15.
 */
public class MyLocationListener implements LocationListener{

    private static final long MIN_TIME = 100;
    private static final float MIN_DISTANCE = 100;
    private static MyLocationListener INSTANCE;
    public LatLng lastKnowLatLng;
    private LocationManager locationManager;

    public MyLocationListener(Context context)
    {
    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    public static void init(Context context)
    {
        if(null == INSTANCE)
        {
            INSTANCE = new MyLocationListener(context);
        }
    }

    public static MyLocationListener getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnowLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
