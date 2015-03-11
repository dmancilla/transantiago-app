package cl.gob.modernizacion.itransantiago.misc;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.gob.modernizacion.itransantiago.models.PuntoBIP;

/**
 * Created by ribarra on 2/13/15.
 */
public class PuntosBIPRenderer extends iTransantiagoRenderer<PuntoBIP> {

    private List<PuntoBIP> puntosBIP;
    public Map<String, PuntoBIP> mapIDPuntoBIP = new HashMap<String, PuntoBIP>();

    public PuntosBIPRenderer(Context context, GoogleMap map, ClusterManager<PuntoBIP> clusterManager, BitmapDescriptor icon) {
        super(context, map, clusterManager, icon, Color.BLUE);

    }

    protected void onClusterItemRendered(PuntoBIP puntoBIP, Marker marker)
    {
        Log.d("iTransantiago", "cluster-item-rendered");
        mapIDPuntoBIP.put(marker.getId(), puntoBIP);
    }


}
