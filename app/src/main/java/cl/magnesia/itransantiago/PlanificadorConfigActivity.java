package cl.magnesia.itransantiago;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static cl.magnesia.itransantiago.Config.TAG;

public class PlanificadorConfigActivity extends Activity implements View.OnFocusChangeListener {

	private Button buttonSwap;
	private EditText textOrigen;
	private EditText textDestino;

	private Geocoder geocoder;

	private LatLng upperLeft = new LatLng(-33.364795, -70.819704);
	private LatLng bottomRight = new LatLng(-33.541818, -70.492861);

	private LatLng lowerLeft = new LatLng(-33.543535, -70.801164);
	private LatLng upperRight = new LatLng(-33.348736, -70.514833);

    private LatLng latLng;

    private boolean origenGPS = false;
    private boolean destinoGPS = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_planificador_config);

		geocoder = new Geocoder(this);

		buttonSwap = (Button) findViewById(R.id.planificador_swap);
		textOrigen = (EditText) findViewById(R.id.planificador_origen);
		textDestino = (EditText) findViewById(R.id.planificador_destino);

        Bundle bundle = getIntent().getExtras();
        latLng = new LatLng(bundle.getDouble("LATITUDE"), bundle.getDouble("LONGITUDE"));

        Log.d("iTransantiago", "lat. " + latLng.latitude + " lng. " + latLng.longitude);
    }

	public void onClick(View view) {

		Log.d(TAG, "view. " + view.getClass());
		Log.d(TAG, "view. " + view.getId());

		if (view.getId() == R.id.planificador_swap) {
			String aux = textOrigen.getText().toString();
			textOrigen.setText(textDestino.getText());
			textDestino.setText(aux);

            if(origenGPS)
            {
                origenGPS = false;
                destinoGPS = true;
            }
            else if(destinoGPS)
            {
                destinoGPS = false;
                origenGPS = true;
            }
		}

		else if (view.getId() == R.id.planificador_back) {
			setResult(Config.ACTIVITY_BACK);
			finish();
		} else if(view.getId() == R.id.planificador_origen_gps) {

            textOrigen.setText("Ubicación actual");
            if(destinoGPS)
            {
                destinoGPS = false;
                textDestino.setText("");
            }
            origenGPS = true;
        } else if(view.getId() == R.id.planificador_destino_gps) {

            textDestino.setText("Ubicación actual");
            if(destinoGPS)
            {
                origenGPS = false;
                textOrigen.setText("");
            }
            destinoGPS = true;
        }

        else {

			String origen = textOrigen.getText().toString();
			String destino = textDestino.getText().toString();

            // origen = "Vergara 471";
            // destino = "Vicuña Mackenna 6000";

			if (0 == origen.length() && !origenGPS) {
				Utils.errorDialog(this,
						"Debe ingresar una dirección de origen.");
				return;
			}
			if (0 == destino.length() && !destinoGPS) {
				Utils.errorDialog(this,
						"Debe ingresar una dirección de destino.");
				return;
			}

			Log.d("iTransantiago", "ruta. " + origen + " => " + destino);

			// String origen = "Vergara 471";
			// String destino = "Sim�n Bol�var 5000";

			final ProgressDialog dialog = ProgressDialog.show(this,
					"iTransantiago", "Cargando");

			try {

                LatLng latLngOrigen;
                LatLng latLngDestino;

                if(origenGPS)
                {
                    Log.d("iTransantiago", "usando origenGPS");
                    latLngOrigen = latLng;
                }
                else
                {
                    List<Address> lstOrigen = geocoder.getFromLocationName(origen,
                            1, lowerLeft.latitude, lowerLeft.longitude,
                            upperRight.latitude, upperRight.longitude);
                    if (lstOrigen.size() < 1) {
                        Utils.errorDialog(this,
                                "La direcci�n de origen no fue encontrada");
                        dialog.dismiss();
                        return;
                    }

                    latLngOrigen = new LatLng(lstOrigen.get(0).getLatitude(), lstOrigen.get(0).getLongitude());
                }

                if(destinoGPS)
                {
                    Log.d("iTransantiago", "usando destinoGPS");
                    latLngDestino = latLng;
                }
                else
                {
                    List<Address> lstDestino = geocoder.getFromLocationName(
                            destino, 1, lowerLeft.latitude, lowerLeft.longitude,
                            upperRight.latitude, upperRight.longitude);


                    if (lstDestino.size() < 1) {
                        Utils.errorDialog(this,
                                "La direcci�n de destino no fue encontrada");
                        dialog.dismiss();
                        return;
                    }

                    latLngDestino = new LatLng(lstDestino.get(0).getLatitude(), lstDestino.get(0).getLongitude());
                }


				RequestQueue queue = Volley.newRequestQueue(this);

				String url = String
						.format("http://itransantiago.modernizacion.gob.cl:8080/opentripplanner-api-webapp/ws/plan?fromPlace=%f,%f&toPlace=%f,%f&maxWalkDistance=1600",
								latLngOrigen.latitude, latLngOrigen.longitude, latLngDestino.latitude, latLngDestino.longitude);

				Log.d("iTransantiago", url);

				// Request a string response from the provided URL.
				JsonObjectRequest request = new JsonObjectRequest(Method.GET,
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

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void handleRespose(JSONObject response) {

		if (response.isNull("plan")) {
			Utils.errorDialog(this, "Ruta no encontrada");

		} else {

			Log.d("iTransantiago", response.getClass().getName());

			Intent data = new Intent();
			data.putExtra("response", response.toString());
			setResult(Config.ACTIVITY_PLANIFICADOR_CONFIG, data);
			finish();
		}

	}

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(view.getId() == R.id.planificador_origen)
        {
            Log.d("iTransantiago", "focus ORIGEN");
            if(origenGPS)
            {
                textOrigen.setText("");
            }
            origenGPS = false;
        }
        else if(view.getId() == R.id.planificador_destino)
        {
            Log.d("iTransantiago", "focus DESTINO");
            if(destinoGPS)
            {
                textDestino.setText("");
            }
            destinoGPS = false;
        }
    }
}
