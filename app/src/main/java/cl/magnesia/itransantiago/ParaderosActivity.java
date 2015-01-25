package cl.magnesia.itransantiago;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import cl.magnesia.itransantiago.misc.MyItem;
import cl.magnesia.itransantiago.models.Paradero;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import static cl.magnesia.itransantiago.Config.TAG;

public class ParaderosActivity extends FragmentActivity {

	private GoogleMap map;

	private List<Paradero> paraderos;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paraderos);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.paraderos_mapa2);
		map = mapFragment.getMap();

		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		MyDatabase db = new MyDatabase(this);

		paraderos = db.getParaderos();

		setUpClusterer();

	}

	private ClusterManager<MyItem> mClusterManager;

	private void setUpClusterer() {
		// Declare a variable for the cluster manager.

		// Position the map.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Config.latLngStgo, 10));

		// Initialize the manager with the context and the map.
		// (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, map);

		// Point the map's listeners at the listeners implemented by the cluster
		// manager.
		map.setOnCameraChangeListener(mClusterManager);
		map.setOnMarkerClickListener(mClusterManager);

		// Add cluster items (markers) to the cluster manager.

		// for (int i = 0; i < 100,paraderos.size(); i++) {

		Drawable drawable = getResources().getDrawable(
				R.drawable.icono_paradero);

		int count = 0;
		for (int i = 0; i < paraderos.size(); i++) {
			Paradero paradero = paraderos.get(i);


            /*
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
					.fromBitmap(((BitmapDrawable) drawable).getBitmap());

			map.addMarker(new MarkerOptions()
					.position(new LatLng(paradero.lat, paradero.lon))
					.icon(bitmapDescriptor).title(paradero.nombre));
					*/

			MyItem item = new MyItem(paradero.latLng.latitude, paradero.latLng.longitude);
			mClusterManager.addItem(item);
		}

	}
}
