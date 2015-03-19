package cl.gob.modernizacion.itransantiago;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.misc.MyLocationListener;

import static cl.gob.modernizacion.itransantiago.Config.TAG;

public class PlanificadorConfigActivity extends BaseActivity implements View.OnFocusChangeListener {

	private Button buttonSwap;
	private EditText textOrigen;
	private EditText textDestino;

	private Geocoder geocoder;

	private LatLng upperLeft = new LatLng(-33.364795, -70.819704);
	private LatLng bottomRight = new LatLng(-33.541818, -70.492861);

	private LatLng lowerLeft = new LatLng(-33.543535, -70.801164);
	private LatLng upperRight = new LatLng(-33.348736, -70.514833);

    private String origen;
    private String destino;

    private boolean origenGPS = false;
    private boolean destinoGPS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        overridePendingTransition(R.animator.activity_from_bottom, R.animator.activity_close_scale);

		setContentView(R.layout.activity_planificador_config);

        // header
        View view = (View)findViewById(R.id.header);
        Button button = (Button) view.findViewById(R.id.header_btn_close);
        button.setVisibility(View.VISIBLE);

        TextView textView = (TextView)view.findViewById(R.id.header_titulo);
        textView.setText("Ruta");

		geocoder = new Geocoder(this);

		buttonSwap = (Button) findViewById(R.id.planificador_swap);
		textOrigen = (EditText) findViewById(R.id.planificador_origen);
		textDestino = (EditText) findViewById(R.id.planificador_destino);

        Utils.trackScreen(this, "planificador-config");

    }

	public void onClick(View view) {

		Log.d(TAG, "view. " + view.getClass());
		Log.d(TAG, "view. " + view.getId());

        if( view.getId() == R.id.header_btn_close)
        {
            finish();
        }
		else if (view.getId() == R.id.planificador_swap) {
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

		else if (view.getId() == R.id.header_btn_buscar) {
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
            if(origenGPS)
            {
                origenGPS = false;
                textOrigen.setText("");
            }
            destinoGPS = true;
        }

        else {

            LatLng latLng = null;
            if(origenGPS || destinoGPS)
            {
                latLng = MyLocationListener.getInstance().lastKnowLatLng;
                if(latLng == null) {
                    Utils.errorDialog(this, "No se ha podido determinar la ubicación actual");
                    return;
                }
            }

			origen = textOrigen.getText().toString();
			destino = textDestino.getText().toString();

            if(origen.length() == 0)
                origen = null;

            if(destino.length() == 0)
                destino = null;

            // origen = "Vergara 471";
            // destino = "Vicuña Mackenna 6000";

			if (null == origen && !origenGPS) {
				Utils.errorDialog(this,
						"Debe ingresar una dirección de origen.");
				return;
			}
			if (null == destino && !destinoGPS) {
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

				e.printStackTrace();

                Utils.errorDialog(this,
                        "No se ha podido determinar la dirección ingresada");
                dialog.dismiss();
                return;
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
            data.putExtra("origen", origen);
            data.putExtra("destino", destino);
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
