package cl.magnesia.itransantiago;

import android.app.ProgressDialog;
import android.location.Address;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class RecorridosParaderoActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorridos_paradero);

        Bundle bundle = getIntent().getExtras();
        String paradero = bundle.getString("PARADERO");

        // header
        Button button = (Button) findViewById(R.id.header_btn_back);
        button.setVisibility(View.VISIBLE);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText(String.format("Paradero %s", paradero));

        findViewById(R.id.recorridos_paradero_row_1).setVisibility(View.GONE);
        findViewById(R.id.recorridos_paradero_row_2).setVisibility(View.GONE);
        findViewById(R.id.recorridos_paradero_row_3).setVisibility(View.GONE);
        findViewById(R.id.recorridos_paradero_row_4).setVisibility(View.GONE);
        findViewById(R.id.recorridos_paradero_row_5).setVisibility(View.GONE);

        String url = String
                .format("%s&paradero=%s", Config.URL_PREDICCION, paradero);

        Log.d("iTransantiago", url);

        final ProgressDialog dialog = ProgressDialog.show(this,
                "iTransantiago", "Cargando");

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray items = response.getJSONObject("servicios").getJSONArray("item");
                    for(int i = 0; i < 5; i++)
                    {
                        int id = 0;
                        switch(i)
                        {
                            case 0:
                                id = R.id.recorridos_paradero_row_1;
                                break;
                            case 1:
                                id = R.id.recorridos_paradero_row_2;
                                break;
                            case 2:
                                id = R.id.recorridos_paradero_row_3;
                                break;
                            case 3:
                                id = R.id.recorridos_paradero_row_4;
                                break;
                            case 4:
                                id = R.id.recorridos_paradero_row_5;
                                break;

                        }
                        View view = (View)findViewById(id);

                        if(i < items.length())
                        {
                            view.setVisibility(View.VISIBLE);

                            JSONObject item = items.getJSONObject(i);

                            Log.d("iTransantiago", item.toString());

                            String servicio = item.getString("servicio");
                            TextView textViewServicio = (TextView)view.findViewById(R.id.recorridos_paradero_servicio);
                            textViewServicio.setText(servicio);

                            String tiempo = item.getString("horaprediccionbus1");
                            TextView textViewTiempo = (TextView)view.findViewById(R.id.recorridos_paradero_tiempo);
                            textViewTiempo.setText(tiempo);

                            int distancia = item.getInt("distanciabus1");
                            TextView textViewDistancia = (TextView)view.findViewById(R.id.recorridos_paradero_distancia);
                            textViewDistancia.setText(String.format("%d mts.", distancia));
                        }
                        else
                        {
                            view.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("iTransantiago", error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public void onClick(View view)
    {
        if(true)
        {
            finish();
        }
    }


}
