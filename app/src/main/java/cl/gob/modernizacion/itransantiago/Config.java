package cl.gob.modernizacion.itransantiago;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Config {

	public static final int ACTIVITY_BACK = -1;
	public static final int ACTIVITY_PLANIFICADOR_CONFIG = 100;
    public static final int ACTIVITY_PLANIFICADOR_ITINERARIO = 101;
    public static final int ACTIVITY_PLANIFICADOR_TRAMO = 102;

    public static final String BUNDLE_PARADERO = "PARADERO";
    public static final String BUNDLE_PUNTO_BIP = "PUNTO_BIP";
    public static final String BUNDLE_SERVICIO = "SERVICIO";

	public static final String TAG = "iTransantiago";

    public static final double MAX_DISTANCE = 50000;

	public static final LatLng latLngStgo = new LatLng(-33.4430, -70.6537);
    public static final LatLngBounds latLngBoundsStgo;
    static {
        latLngBoundsStgo = Utils.boundsWithCenterAndLatLngDistance(latLngStgo, 3000, 3000);
    }

    public static LatLng upperLeft = new LatLng(-33.364795, -70.819704);
    public static LatLng bottomRight = new LatLng(-33.541818, -70.492861);

    public static LatLng lowerLeft = new LatLng(-33.543535, -70.801164);
    public static LatLng upperRight = new LatLng(-33.348736, -70.514833);

    public static final String URL_PREDICCION = "";

    public static final String TRACKER_ID = "UA-23675324-26";
    public static final int TRACKER_TIMEOUT = 300;


}
