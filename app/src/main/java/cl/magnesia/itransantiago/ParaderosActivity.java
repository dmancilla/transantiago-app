package cl.magnesia.itransantiago;

import java.util.List;

import cl.magnesia.itransantiago.db.MyDatabase;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static cl.magnesia.itransantiago.Config.TAG;

public class ParaderosActivity extends BaseFragmentActivity implements GoogleMap.OnMapLoadedCallback {

    // UI
	private GoogleMap map;
    private ProgressDialog dialog;

    // data
	private List<Paradero> paraderos;

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
        map.setOnMapLoadedCallback(this);
        map.setClustering(new ClusteringSettings().clusterOptionsProvider(new MyClusterOptionsProvider(this)).enabled(true).addMarkersDynamically(true);

        // SETUP HEADER
        // header
        View view = (View)findViewById(R.id.header);
        Button button = (Button) view.findViewById(R.id.header_btn_buscar);
        button.setVisibility(View.VISIBLE);

        loadParaderosBackground();

	}

    private void loadParaderosBackground()
    {
        dialog = new ProgressDialog(this, R.style.MyTheme);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.show();
        new AsyncTask<Object, Void, Object>()
        {

            private MyDatabase database = new MyDatabase(getApplicationContext());

            @Override
            protected Object doInBackground(Object[] params) {
                paraderos = database.getParaderos();


                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                addMarkers();
            }
        }.execute();
    }

    public void addMarkers()
    {

        runOnUiThread(new Runnable()
        {

            @Override
            public void run() {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icono_paradero);

                for (Paradero paradero : paraderos)
                    map.addMarker(new MarkerOptions().position(paradero.latLng).icon(icon));

                dialog.dismiss();
            }
        });
    }

    @Override
    public void onMapLoaded() {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(Config.latLngBoundsStgo, 0));
    }
}
