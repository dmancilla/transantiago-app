package cl.magnesia.itransantiago.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

import cl.magnesia.itransantiago.misc.MyItem;

public class PuntoBIP implements Serializable, MyItem {

    public String entidad;
    public String direccion;
    public String comuna;
    public String horario;

    public transient LatLng latLng;
    public double lat;
    public double lng;

    public PuntoBIP(String entidad, String direccion, String comuna, String horario, LatLng latLng)
    {
        this.entidad = entidad;
        this.direccion = direccion;
        this.comuna = comuna;
        this.horario = horario;
        this.latLng = latLng;

        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

    @Override
    public LatLng getPosition()
    {
        return latLng;
    }

    @Override
    public String getTitle()
    {
        return entidad;
    }

    @Override
    public String getSnippet()
    {
        return direccion;
    }

}
