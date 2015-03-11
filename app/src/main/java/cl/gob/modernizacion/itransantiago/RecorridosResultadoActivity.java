package cl.gob.modernizacion.itransantiago;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.db.MyDatabase;
import cl.gob.modernizacion.itransantiago.models.Paradero;
import cl.gob.modernizacion.itransantiago.models.Ruta;
import cl.gob.modernizacion.itransantiago.models.Trip;


public class RecorridosResultadoActivity extends BaseFragmentActivity implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;
    private TextView textHeader;

    private LinearLayout layoutDireccion;
    private TextView textDireccion;

    // estado
    private Ruta ruta;
    private Trip trip;
    private List<LatLng> points;
    private List<Paradero> paraderos;

    private String servicio;
    private int direccion = 0;
    private Map<String, Paradero> paraderosMap = new HashMap<String, Paradero>();

    // UI
    private ProgressDialog dialog;

    // zoom checker
    private Handler handler;
    private Runnable zoomChecker;
    public static final int zoomCheckingDelay = 500;

    private List<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recorridos_resultado);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        servicio = bundle.getString("SERVICIO");

        // header
        Button button = (Button) findViewById(R.id.header_btn_back);
        button.setVisibility(View.VISIBLE);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText(String.format("Recorrido %s", servicio));

        layoutDireccion = (LinearLayout) findViewById(R.id.recorridos_layout_direccion);
        layoutDireccion.setVisibility(View.GONE);

        textDireccion = (TextView) findViewById(R.id.recorridos_direccion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.recorridos_mapa);
        map = mapFragment.getMap();
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setOnMapLoadedCallback(this);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);


        // check zoom
        handler = new Handler();
        zoomChecker = new Runnable()
        {
            public void run()
            {
                handler.removeCallbacks(zoomChecker);
                handler.postDelayed(zoomChecker, zoomCheckingDelay);

                Log.d("iTransantiago", "zoom. " + map.getCameraPosition().zoom);

                for(Marker marker : markers)
                {
                    marker.setVisible(map.getCameraPosition().zoom > 14);
                }
            }
        };

        final Handler myHandler = new Handler();
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                displayRecorrido();
            }
        };
        myHandler.postDelayed(runnable, 1500);//Message will be delivered in 1 second.

        overridePendingTransition(R.animator.activity_from_right, R.animator.activity_close_scale);

    }

    public void onResume()
    {
        super.onResume();

        handler.postDelayed(zoomChecker, zoomCheckingDelay);


    }

    public void onClick(View view)
    {
        if (view.getId() == R.id.header_btn_back) {
            setResult(Config.ACTIVITY_BACK);
            finish();
        }
        else if(view.getId() == R.id.recorridos_layout_direccion)
        {
            direccion = 1 - direccion;
            displayRecorrido();
        }
    }

    private void displayRecorrido()
    {


            dialog = new ProgressDialog(this, R.style.MyTheme);
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.show();

            layoutDireccion.setVisibility(View.GONE);

            new AsyncTask<Object, Void, Object>()
            {

                private MyDatabase database = new MyDatabase(getApplicationContext());

                @Override
                protected Object doInBackground(Object[] params) {

                    Log.d("iTransantiago", "servicio. " + servicio);


                    ruta = database.getRuta(servicio);
                    if(ruta == null)
                        return null;

                    trip = database.getTripByRouteId(servicio, direccion);

                    points = database.getShapeByTripId(trip.tripID);
                    paraderos = database.getParaderosByTripId(trip.tripID);


                    return null;
                }

                @Override
                protected void onPostExecute(Object result) {


                        setupRecorrido();

                }
            }.execute();



    }

    public void setupRecorrido()
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run() {

                map.clear();

                LatLngBounds bounds = LatLngBounds
                        .builder()
                        .include(
                                new LatLng(points.get(0).latitude, points.get(0).longitude)).build();
                for (int j = 0; j < points.size(); j++) {
                    bounds = bounds.including(points.get(j));
                }
                map.addPolyline(new PolylineOptions().addAll(points).color(getResources().getColor(R.color.polyline_bus)));

                // agrega paraderos
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_bus);
                for(Paradero paradero : paraderos)
                {
                    MarkerOptions markerOptions = new MarkerOptions().position(paradero.latLng)
                            .title("Paradero de Bus")
                            .snippet(paradero.name)
                            .icon(icon)
                            .anchor(0.0f, 1.0f);

                    Marker marker = map.addMarker(markerOptions);
                    paraderosMap.put(marker.getId(), paradero);
                    markers.add(marker);
                }

                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                layoutDireccion.setVisibility(View.VISIBLE);
                textDireccion.setText(String.format("Dirección: Hacia %s", trip.tripHeadSign));

                dialog.dismiss();
            }
        });
    }

    // InfowWindowAdapter
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);

        view.findViewById(R.id.marker_btn_disclosure).setVisibility(View.VISIBLE);
        view.findViewById(R.id.marker_btn_info).setVisibility(View.GONE);

        TextView textTitulo = (TextView) view.findViewById(R.id.marker_titulo);
        textTitulo.setText(marker.getTitle());

        TextView textDescripcion = (TextView) view.findViewById(R.id.marker_descripcion);
        textDescripcion.setText(marker.getSnippet());

        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {


        handler.removeCallbacks(zoomChecker);

        Paradero paradero = paraderosMap.get(marker.getId());

        Intent intent = new Intent(this, RecorridosParaderoActivity.class);
        intent.putExtra(Config.BUNDLE_PARADERO, paradero);

        startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_TRAMO);

    }

    @Override
    public void onMapLoaded() {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(Config.latLngBoundsStgo, 0));
    }
}
