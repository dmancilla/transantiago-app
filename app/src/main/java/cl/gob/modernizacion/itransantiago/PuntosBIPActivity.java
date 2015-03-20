package cl.gob.modernizacion.itransantiago;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.gob.modernizacion.itransantiago.misc.MyLocationListener;
import cl.gob.modernizacion.itransantiago.R;
import cl.gob.modernizacion.itransantiago.db.MyDatabase;
import cl.gob.modernizacion.itransantiago.misc.MyClusterOptionsProvider;
import cl.gob.modernizacion.itransantiago.models.PuntoBIP;

public class PuntosBIPActivity extends BaseFragmentActivity implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLoadedCallback, GoogleMap.InfoWindowAdapter {


    // UI
	private GoogleMap map;
    private Button buttonMyLocation;

    // ESTADO
	private List<PuntoBIP> puntosBIP;
    private Map<String, PuntoBIP> markers = new HashMap<String, PuntoBIP>();
    private boolean myLocationEnabled = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puntos_bip);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText("Puntos BIP");

		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.puntos_bip_mapa);
        map = fragment.getExtendedMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setOnMapLoadedCallback(this);
        map.setClustering(new ClusteringSettings().clusterOptionsProvider(new MyClusterOptionsProvider(this, R.color.marker_blue)).enabled(true).addMarkersDynamically(true));

        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);

        buttonMyLocation = (Button) findViewById(R.id.mapa_my_location);

		MyDatabase db = new MyDatabase(this);

		puntosBIP = db.getPuntosBIP();

		setUpClusterer();

        Utils.trackScreen(this, "puntos bip!");

	}



	private void setUpClusterer() {
		// Declare a variable for the cluster manager.

		// Position the map.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Config.latLngStgo, 10));

        BitmapDescriptor icon = BitmapDescriptorFactory
                .fromBitmap(((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.bip_annotation)).getBitmap());




		for (PuntoBIP puntoBIP : puntosBIP) {

            Marker marker = map.addMarker(new MarkerOptions().title(puntoBIP.entidad).snippet(puntoBIP.direccion).position(puntoBIP.latLng).icon(icon));
            marker.setData(puntoBIP);

		}

	}

    public void onClick(View view)
    {
        if( view.getId() == R.id.mapa_my_location )
        {
            myLocationEnabled = !myLocationEnabled;

            map.setMyLocationEnabled(myLocationEnabled);
            if(myLocationEnabled)
            {
                buttonMyLocation.setBackgroundResource(R.drawable.locate_on);

                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(MyLocationListener.getInstance().lastKnowLatLng);
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                map.moveCamera(center);
                map.animateCamera(zoom);
            }
            else
            {
                buttonMyLocation.setBackgroundResource(R.drawable.locate);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {

        Log.d("iTransantiago", "lolo");
        return null;

    }

    @Override
    public View getInfoContents(Marker marker) {

        if(marker.isCluster())
            return null;

        Log.d("iTransantiago", "lala");

        View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);

        TextView textTitulo = (TextView) view.findViewById(R.id.marker_titulo);
        textTitulo.setText(marker.getTitle());

        TextView textDescripcion = (TextView) view.findViewById(R.id.marker_descripcion);
        textDescripcion.setText(marker.getSnippet());

        view.findViewById(R.id.marker_btn_disclosure).setVisibility(View.GONE);
        view.findViewById(R.id.marker_btn_info).setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        PuntoBIP puntoBIP = (PuntoBIP) marker.getData();

        Intent intent = new Intent(this, PuntosBIPDetalleActivity.class);
        intent.putExtra(Config.BUNDLE_PUNTO_BIP, puntoBIP);
        startActivity(intent);

    }

    @Override
    public void onMapLoaded() {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(Config.latLngBoundsStgo, 0));
    }
}
