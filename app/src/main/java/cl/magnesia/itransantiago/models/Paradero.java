package cl.magnesia.itransantiago.models;

import com.google.android.gms.maps.model.LatLng;

public class Paradero {

    public String stopID;
    public String name;
    public String code;

    public LatLng latLng;

	public Paradero(String stopID, String code, String name, double lat, double lng) {

        this.stopID = stopID;
        this.code = code;
		this.name = name;

        this.latLng = new LatLng(lat, lng);
	}

}
