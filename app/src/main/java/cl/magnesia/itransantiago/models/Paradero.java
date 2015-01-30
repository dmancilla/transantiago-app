package cl.magnesia.itransantiago.models;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paradero  extends SugarRecord<Paradero> implements Serializable
{

    public String stopID;
    public String name;
    public String code;

    @Ignore
    public transient LatLng latLng;

    public Paradero()
    {

    }

	public Paradero(String stopID, String code, String name, double lat, double lng) {

        this.stopID = stopID;
        this.code = code;
		this.name = name;

        this.latLng = new LatLng(lat, lng);
	}

}
