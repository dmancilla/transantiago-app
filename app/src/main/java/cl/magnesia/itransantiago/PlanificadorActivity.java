package cl.magnesia.itransantiago;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static cl.magnesia.itransantiago.Config.TAG;

public class PlanificadorActivity extends FragmentActivity implements
		LocationListener, GoogleMap.OnMyLocationChangeListener {

	private GoogleMap map;
    private RelativeLayout layoutResultados;

    private TextView textViewDuracion;
    private TextView textViewBadge;

	private LocationManager locationManager;

    private LatLng lastKnowLatLng;

	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;

	private static final int colorWalk = Color.rgb(0, 172, 235);
	private static final int colorBus = Color.rgb(116, 176, 19);
	private static final int colorSubway = Color.rgb(227, 0, 0);

    // estado
    private JSONArray itineraries;
    private JSONObject plan;
    private int selectedItinerario;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//CalligraphyConfig.initDefault("fonts/TSInfReg.otf", R.attr.fontPath);

		setContentView(R.layout.activity_planificador);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.planificador_mapa);
		map = mapFragment.getMap();

		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setMyLocationEnabled(true);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        layoutResultados = (RelativeLayout) findViewById(R.id.planificador_resultados);
        layoutResultados.setVisibility(View.GONE);

        textViewDuracion = (TextView) findViewById(R.id.planificador_resultados_duracion);
        textViewBadge = (TextView) findViewById(R.id.planificador_text_view_mas_rutas);

	}

	public void onClick(View view) {
		System.out.println("click");
		Log.d("iTransantiago", "click");
		if (view.getId() == R.id.planificador_btn_buscar) // TODO: check button
		{
			Intent intent = new Intent(this, PlanificadorConfigActivity.class);

            double lat = map.getMyLocation().getLatitude();
            double lng = map.getMyLocation().getLongitude();

            intent.putExtra("LATITUDE", lat);
            intent.putExtra("LONGITUDE", lng);

			startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_CONFIG);

		}
        else if(view.getId() == R.id.planificador_btn_mas_rutas)
        {
            Intent intent = new Intent(this, PlanificadorItinerariosActivity.class);
            intent.putExtra("ITINERARIOS", itineraries.toString());
            intent.putExtra("ITINERARIO", selectedItinerario);
            startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_CONFIG);
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

				Bundle bundle = data.getExtras();
				JSONObject response = new JSONObject(
						bundle.getString("response"));

				plan = response.getJSONObject("plan");
                itineraries = plan.getJSONArray("itineraries");

                displayRuta();


			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
            }

        }
	}

    private void displayRuta() throws JSONException {
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

        map.addMarker(new MarkerOptions()
                .position(
                        new LatLng(from.getDouble("lat"), from
                                .getDouble("lon"))).icon(
                        bitmapDescriptorFrom));

        map.addMarker(new MarkerOptions().position(
                new LatLng(to.getDouble("lat"), to.getDouble("lon")))
                .icon(bitmapDescriptorTo));

        for (int i = 0; i < legs.length(); i++) {
            JSONObject leg = legs.getJSONObject(i);

            double lat = leg.getJSONObject("from").getDouble("lat");
            double lon = leg.getJSONObject("from").getDouble("lon");
            String mode = leg.getString("mode");

            String title = "";
            int color = 0;
            BitmapDescriptor bitmapDescriptor = null;
            float u = 0.0f;
            float v = 0.0f;
            if (mode.equals("WALK")) {
                title = "Caminar";
                color = colorWalk;
                bitmapDescriptor = bitmapDescriptorWalk;
                u = 1.0f;
                v = 1.0f;
            } else if (mode.equals("BUS")) {
                title = String.format("BUS %s", leg.getString("route"));
                color = colorBus;
                bitmapDescriptor = bitmapDescriptorBus;
                u = 0.0f;
                v = 1.0f;
            } else if (mode.equals("SUBWAY")) {
                title = String.format("BUS %s", leg.getString("route"));
                color = colorSubway;
                bitmapDescriptor = bitmapDescriptorSubway;
                u = 0.0f;
                v = 1.0f;
            }

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

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon)).title(title)
                    .snippet("-")
                    .icon(bitmapDescriptor).anchor(u, v));
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
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				15);
		map.animateCamera(cameraUpdate);
		locationManager.removeUpdates(this);

	}

    @Override
    public void onMyLocationChange(Location location) {
        lastKnowLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }
}
