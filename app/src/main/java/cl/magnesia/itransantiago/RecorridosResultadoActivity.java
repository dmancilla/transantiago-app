package cl.magnesia.itransantiago;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.magnesia.itransantiago.models.Paradero;
import cl.magnesia.itransantiago.models.Ruta;
import cl.magnesia.itransantiago.models.Tramo;
import cl.magnesia.itransantiago.models.Trip;


public class RecorridosResultadoActivity extends BaseFragmentActivity implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap map;
    private TextView textHeader;

    private LinearLayout layoutDireccion;
    private TextView textDireccion;

    // estado
    private Ruta ruta;
    private String servicio;
    private int direccion = 0;
    private Map<String, String> paraderosCodes = new HashMap<String, String>();

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
            Log.d("iTransantiago", "layout....");
            direccion = 1 - direccion;
            displayRecorrido();
        }
    }

    private void displayRecorrido()
    {

        map.clear();
        layoutDireccion.setVisibility(View.VISIBLE);

        Log.d("iTransantiago", "servicio. " + servicio);

        MyDatabase db = new MyDatabase(this);
        ruta = db.getRuta(servicio);
        if(null == ruta)
        {
            Utils.errorDialog(this,
                    "Servicio no encontrado.");
            return;
        }

        Trip trip = db.getTripByRouteId(servicio, direccion);

        List<LatLng> points = db.getShapeByTripId(trip.tripID);
        List<Paradero> paraderos = db.getParaderosByTripId(trip.tripID);


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
            paraderosCodes.put(marker.getId(), paradero.code);
            markers.add(marker);
        }

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        textDireccion.setText(String.format("Dirección: Hacia %s", trip.tripHeadSign));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recorridos_resultado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        String code = paraderosCodes.get(marker.getId());

        Log.d("iTransantiago", "click. " + code);

        Intent intent = new Intent(this, RecorridosParaderoActivity.class);
        intent.putExtra("PARADERO", code);

        startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_TRAMO);

    }

}
