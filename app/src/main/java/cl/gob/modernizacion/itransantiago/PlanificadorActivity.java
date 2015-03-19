package cl.gob.modernizacion.itransantiago;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.gob.modernizacion.itransantiago.misc.MyLocationListener;
import cl.gob.modernizacion.itransantiago.misc.SessionManager;
import cl.gob.modernizacion.itransantiago.models.Tramo;
import cl.gob.modernizacion.itransantiago.models.Viaje;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cl.magnesia.itransantiago.R;

import static cl.gob.modernizacion.itransantiago.Config.TAG;

public class PlanificadorActivity extends BaseFragmentActivity implements
        GoogleMap.OnMyLocationChangeListener, GoogleMap.OnInfoWindowClickListener {

    private static final int colorWalk = Color.rgb(0, 172, 235);
    private static final int colorBus = Color.rgb(116, 176, 19);
    private static final int colorSubway = Color.rgb(227, 0, 0);

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private RelativeLayout layoutResultados;

    private Button buttonFavorito;
    private TextView textViewDuracion;
    private TextView textViewBadge;

    private Button buttonMyLocation;

    // estado
    private Viaje viaje;
    private String origen;
    private String destino;

    private JSONObject response;
    private JSONArray itineraries;
    private JSONObject plan;
    private int selectedItinerario;

    private Map<String, Tramo> tramos = new HashMap<String, Tramo>();

    private boolean myLocationEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_planificador);

        // header
        View view = (View)findViewById(R.id.header);
        Button button = (Button) view.findViewById(R.id.header_btn_buscar);
        button.setVisibility(View.VISIBLE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.planificador_mapa);

        map = mapFragment.getMap();
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);

                TextView textTitulo = (TextView) view.findViewById(R.id.marker_titulo);
                textTitulo.setText(marker.getTitle());

                TextView textDescripcion = (TextView) view.findViewById(R.id.marker_descripcion);
                textDescripcion.setText(marker.getSnippet());

                return view;
            }
        });
        map.setOnInfoWindowClickListener(this);



        layoutResultados = (RelativeLayout) findViewById(R.id.planificador_resultados);
        layoutResultados.setVisibility(View.GONE);

        buttonFavorito = (Button) findViewById(R.id.planificador_resultados_guardar);
        textViewDuracion = (TextView) findViewById(R.id.planificador_resultados_duracion);
        textViewBadge = (TextView) findViewById(R.id.planificador_text_view_mas_rutas);

        buttonMyLocation = (Button) findViewById(R.id.mapa_my_location);

        // chequea si viene de otra actividad

        if(SessionManager.getInstance().to != null)
        {

            view.findViewById(R.id.header_btn_buscar).setVisibility(View.GONE);
            view.findViewById(R.id.header_btn_back).setVisibility(View.VISIBLE);

            // iniciar una búsqueda
            LatLng to = SessionManager.getInstance().to;

            Log.d("iTransantiago", "from => " + MyLocationListener.getInstance().lastKnowLatLng);
            Log.d("iTransantiago", "to => " + to.latitude);

            RequestQueue queue = Volley.newRequestQueue(this);

            String url = String
                    .format("http://itransantiago.modernizacion.gob.cl:8080/opentripplanner-api-webapp/ws/plan?fromPlace=%f,%f&toPlace=%f,%f&maxWalkDistance=1600",
                            MyLocationListener.getInstance().lastKnowLatLng.latitude, MyLocationListener.getInstance().lastKnowLatLng.longitude, to.latitude, to.longitude);

            Log.d("iTransantiago", url);

            final ProgressDialog dialog = ProgressDialog.show(this,
                    "iTransantiago", "Cargando");

            // Request a string response from the provided URL.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    dialog.dismiss();

                    handleRespose(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("iTransantiago", error.getMessage());
                }
            });
            // Add the request to the RequestQueue.
            queue.add(request);


            // limpia la sesión
            SessionManager.getInstance().to = null;

        }

        Utils.trackScreen(this, "planificador");
    }

    public void onClick(View view) {
        Log.d("iTransantiago", "click");
        if (view.getId() == R.id.header_btn_buscar)
        {
            Intent intent = new Intent(this, PlanificadorConfigActivity.class);
            startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_CONFIG);

            Utils.trackEvent(this, "button-click", "planificador", "search");

        }
        else if (view.getId() == R.id.header_btn_back)
        {
            finish();

            Utils.trackEvent(this, "button-click", "planificador", "back");

        }
        else if(view.getId() == R.id.planificador_btn_mas_rutas)
        {
            Intent intent = new Intent(this, PlanificadorItinerariosActivity.class);
            intent.putExtra("ITINERARIOS", itineraries.toString());
            intent.putExtra("ITINERARIO", selectedItinerario);
            startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_CONFIG);
        }
        else if(view.getId() == R.id.planificador_resultados_guardar)
        {



            if(null == viaje)
            {
                // crea un favorito
                viaje = new Viaje(origen, destino);
                viaje.save();

                Toast.makeText(this, "Favorito guardado", Toast.LENGTH_SHORT).show();
                buttonFavorito.setBackgroundResource(R.drawable.btn_desguardar_ruta);

                Utils.trackEvent(this, "button-click", "planificador", "save-favorite");


            }
            else
            {
                // elimina un favorito
                viaje.delete();
                viaje = null;

                Toast.makeText(this, "Favorito eliminado", Toast.LENGTH_SHORT).show();
                buttonFavorito.setBackgroundResource(R.drawable.btn_guardar_ruta);

                Utils.trackEvent(this, "button-click", "planificador", "remove-favorite");
            }
        }
        else if( view.getId() == R.id.mapa_my_location )
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "requestCode. " + requestCode);
        Log.d(TAG, "resultCode. " + resultCode);

        if (Config.ACTIVITY_BACK == resultCode) {

        }

        else if (Config.ACTIVITY_PLANIFICADOR_CONFIG == resultCode) {

            try {

                selectedItinerario = 0;
                viaje = null;

                Bundle bundle = data.getExtras();
                origen = bundle.getString("origen");
                destino = bundle.getString("destino");
                response = new JSONObject(
                        bundle.getString("response"));

                plan = response.getJSONObject("plan");
                itineraries = plan.getJSONArray("itineraries");

                displayRuta();


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        else if (Config.ACTIVITY_PLANIFICADOR_ITINERARIO == resultCode) {



            Bundle bundle = data.getExtras();
            selectedItinerario = bundle.getInt("response");

            Log.d("iTransantiago", ""+ selectedItinerario);
            try {
                displayRuta();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    private void displayRuta() throws JSONException, UnsupportedEncodingException {
        JSONObject itinerary = itineraries
                .getJSONObject(selectedItinerario);

        JSONObject from = plan.getJSONObject("from");
        JSONObject to = plan.getJSONObject("to");


        map.clear();

        long duracion = itinerary.getInt("duration");

        JSONArray legs = itinerary.getJSONArray("legs");

        LatLngBounds bounds = LatLngBounds
                .builder()
                .include(
                        new LatLng(from.getDouble("lat"), from
                                .getDouble("lon"))).build();

        BitmapDescriptor bitmapDescriptorBus = BitmapDescriptorFactory
                .fromBitmap(((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.icon_bus)).getBitmap());

        BitmapDescriptor bitmapDescriptorWalk = BitmapDescriptorFactory
                .fromBitmap(((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.icon_walk)).getBitmap());

        BitmapDescriptor bitmapDescriptorSubway = BitmapDescriptorFactory
                .fromBitmap(((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.icon_subway))
                        .getBitmap());

        BitmapDescriptor bitmapDescriptorFrom = BitmapDescriptorFactory
                .fromBitmap(((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.icono_origen))
                        .getBitmap());

        BitmapDescriptor bitmapDescriptorTo = BitmapDescriptorFactory
                .fromBitmap(((BitmapDrawable) getResources()
                        .getDrawable(R.drawable.icono_destino))
                        .getBitmap());

        map.addMarker(new MarkerOptions().title("Origen")
                .position(
                        new LatLng(from.getDouble("lat"), from
                                .getDouble("lon"))).icon(
                        bitmapDescriptorFrom));

        map.addMarker(new MarkerOptions().title("Destino").position(
                new LatLng(to.getDouble("lat"), to.getDouble("lon")))
                .icon(bitmapDescriptorTo));

        for (int i = 0; i < legs.length(); i++) {
            JSONObject leg = legs.getJSONObject(i);

            String mode = leg.getString("mode");

            double lat = leg.getJSONObject("from").getDouble("lat");
            double lon = leg.getJSONObject("from").getDouble("lon");


            String title = "";
            String snippet = "";
            int color = 0;
            BitmapDescriptor bitmapDescriptor = null;
            float u = 0.0f;
            float v = 0.0f;
            if (mode.equals("WALK")) {
                title = "Caminar";
                snippet = Utils.toUTF8(String.format("Camine hasta %s", leg.getJSONObject("from").getString("name")));
                color = colorWalk;
                bitmapDescriptor = bitmapDescriptorWalk;
                u = 1.0f;
                v = 1.0f;
            } else if (mode.equals("BUS")) {
                title = String.format("BUS %s", leg.getString("route"));
                snippet = String.format("%s (%s)", leg.getJSONObject("from").getString("name"),
                        leg.getJSONObject("from").getString("stopCode"));
                color = colorBus;
                bitmapDescriptor = bitmapDescriptorBus;
                u = 0.0f;
                v = 1.0f;
            } else if (mode.equals("SUBWAY")) {
                title = Utils.toUTF8(String.format(leg.getString("route")));
                snippet = Utils.toUTF8(String.format("Metro %s", leg.getString("route")));
                color = colorSubway;
                bitmapDescriptor = bitmapDescriptorSubway;
                u = 0.0f;
                v = 1.0f;
            }

            Tramo tramo = new Tramo(mode, title, leg.getJSONObject("from").getString("name"), leg.getJSONObject("from").getString("stopCode"), leg.getJSONObject("to").getString("name"), leg.getJSONObject("to").getString("stopCode"), leg.getLong("duration"), leg.getLong("distance"));

            String encodedPoints = leg.getJSONObject("legGeometry")
                    .getString("points");

            Log.d("iTransantiago", "encoded. " + i + " -> "
                    + encodedPoints);

            List<LatLng> points = decodePoly(encodedPoints);
            for (int j = 0; j < points.size(); j++) {

                bounds = bounds.including(points.get(j));
            }

            map.addPolyline(new PolylineOptions().addAll(points).color(
                    color));

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon)).title(title)
                    .snippet(snippet)
                    .icon(bitmapDescriptor).anchor(u, v));

            tramos.put(marker.getId(), tramo);
        }

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        textViewDuracion.setText(String.format("%d minutos", Math.round(duracion / (1000 * 60))));
        textViewBadge.setText(String.format("%d", itineraries.length()));
        layoutResultados.setVisibility(View.VISIBLE);
    }

    /**
     * Method to decode polyline points Courtesy :
     * jeffreysambells.com/2010/05/27
     * /decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMyLocationChange(Location location) {
        // lastKnowLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d("iTransantiago", "click");
        Tramo tramo = tramos.get(marker.getId());

        Intent intent = new Intent(this, PlanificadorTramoActivity.class);
        intent.putExtra("TRAMO", tramo);

        startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_TRAMO);

    }


    public void handleRespose(JSONObject response) {

        if (response.isNull("plan")) {
            Utils.errorDialog(this, "Ruta no encontrada");

        } else {
            try {


                Log.d("iTransantiago", response.toString());

                selectedItinerario = 0;
                viaje = null;


                this.response = response;

                plan = response.getJSONObject("plan");
                itineraries = plan.getJSONArray("itineraries");

                displayRuta();
            }
            catch(JSONException exception)
            {
                exception.printStackTrace();
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }

}
