package cl.magnesia.itransantiago;

import java.util.List;

import cl.magnesia.itransantiago.misc.MyClusterOptionsProvider;
import cl.magnesia.itransantiago.misc.iTransantiagoRenderer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import cl.magnesia.itransantiago.misc.MyItem;
import cl.magnesia.itransantiago.models.Paradero;

import com.androidmapsextensions.ClusterOptions;
import com.androidmapsextensions.ClusterOptionsProvider;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static cl.magnesia.itransantiago.Config.TAG;

public class ParaderosActivity extends BaseFragmentActivity implements GoogleMap.OnCameraChangeListener {

	private GoogleMap map;

	private List<Paradero> paraderos;

    private ClusterManager<Paradero> clusterManager;

    private CameraPosition mPreviousCameraPosition;
    private int mPreviousZoom;

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        int currentZoom = (int)cameraPosition.zoom;

        if(Math.abs(mPreviousZoom - currentZoom ) > 0)
        {
            clusterManager.onCameraChange(cameraPosition);
        }

        mPreviousZoom = currentZoom;

        Log.d("iTransantiago", "" + cameraPosition.zoom);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paraderos);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText("Paraderos");

		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.paraderos_mapa2);
		map = fragment.getExtendedMap();
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setClustering(new ClusteringSettings().clusterOptionsProvider(new MyClusterOptionsProvider(this)).addMarkersDynamically(true));

        // SETUP HEADER
        // header
        View view = (View)findViewById(R.id.header);
        Button button = (Button) view.findViewById(R.id.header_btn_buscar);
        button.setVisibility(View.VISIBLE);

		MyDatabase db = new MyDatabase(this);

		paraderos = db.getParaderos();

		setUpClusterer();

	}




	private void setUpClusterer() {
		// Declare a variable for the cluster manager.

		// Position the map.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Config.latLngStgo, 10));

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icono_paradero);


        //clusterManager = new ClusterManager<Paradero>(this, map);
        //clusterManager.setRenderer(new ParaderosRenderer());



		// Point the map's listeners at the listeners implemented by the cluster
		// manager.
		//map.setOnCameraChangeListener(this);
        //map.setOnMarkerClickListener(clusterManager);


		// Add cluster items (markers) to the cluster manager.

		// for (int i = 0; i < 100,paraderos.size(); i++) {

		Drawable drawable = getResources().getDrawable(
				R.drawable.icono_paradero);

		int count = 0;
		for (int i = 0; i < paraderos.size(); i++) {

			Paradero paradero = paraderos.get(i);
			// clusterManager.addItem(paradero);


            map.addMarker(new MarkerOptions().position(paradero.latLng).icon(icon));
		}

        // clusterManager.cluster();

	}
}
