package cl.gob.modernizacion.itransantiago.models;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import cl.gob.modernizacion.itransantiago.misc.MyItem;

public class Paradero  extends SugarRecord<Paradero> implements Serializable, MyItem
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

    public static Paradero findByStopID(String stopID)
    {
        // HINT. SugarRecord transforma stopID a stop_ID
        // https://github.com/satyan/sugar/issues/137

        List<Paradero> paraderos = Paradero.find(Paradero.class, "stop_ID = ?", stopID);
        return paraderos.isEmpty() ? null : paraderos.get(0);
    }

    public static Paradero findByCode(String code)
    {
        // HINT. SugarRecord transforma stopID a stop_ID
        // https://github.com/satyan/sugar/issues/137

        List<Paradero> paraderos = Paradero.find(Paradero.class, "code = ?", code);
        return paraderos.isEmpty() ? null : paraderos.get(0);
    }

    public static Paradero[] all()
    {
        int count = (int)Paradero.count(Paradero.class, "", null);
        Iterator<Paradero> iterator = Paradero.findAll(Paradero.class);

        Paradero[] paraderos = new Paradero[count];
        int i = 0;
        while(iterator.hasNext())
        {
            paraderos[i++] = iterator.next();
        }

        return paraderos;

    }


    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return code;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }
}
