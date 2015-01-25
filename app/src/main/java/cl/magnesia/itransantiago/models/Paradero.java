package cl.magnesia.itransantiago.models;

import com.google.android.gms.maps.model.LatLng;

public class Paradero {

    public String stopID;
    public String name;

    public LatLng latLng;

	public Paradero(String stopID, String name, double lat, double lng) {

        this.stopID = stopID;
		this.name = name;

        this.latLng = new LatLng(lat, lng);
	}

}
