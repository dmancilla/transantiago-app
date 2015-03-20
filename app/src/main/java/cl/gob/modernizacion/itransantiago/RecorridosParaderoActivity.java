package cl.gob.modernizacion.itransantiago;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.gob.modernizacion.itransantiago.R;
import cl.gob.modernizacion.itransantiago.models.Paradero;
import cl.gob.modernizacion.itransantiago.widgets.RecorridosParaderoAdapter;


public class RecorridosParaderoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public Button btnFavorito;
    public ListView listView;

    public Paradero paradero;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorridos_paradero);

        Bundle bundle = getIntent().getExtras();
        paradero = (Paradero) bundle.getSerializable(Config.BUNDLE_PARADERO);

        // header
        Button button = (Button) findViewById(R.id.header_btn_back);
        button.setVisibility(View.VISIBLE);

        button = (Button) findViewById(R.id.header_btn_favorito);
        button.setVisibility(View.VISIBLE);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText(String.format("Paradero %s", paradero.code));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.recorridos_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ListView) findViewById(R.id.recorridos_paradero_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView<?> parent, final View view,
                                                                    int position, long id) {
                                                Log.d("iTransantiago", "oli");
                                            }
                                        });

        // chequear favorito
        btnFavorito = (Button) findViewById(R.id.header_btn_favorito);

        if(null == Paradero.findByStopID(paradero.stopID))
        {
            btnFavorito.setBackgroundResource(R.drawable.btn_guardar);
        }
        else
        {
            btnFavorito.setBackgroundResource(R.drawable.btn_desguardar);
        }

        load();
        Utils.trackScreen(this, "recorridos-paradero");
    }


    public void onClick(View view)
    {
        Log.d("iTransantiago", "click....");
        if(view.getId() == R.id.header_btn_back)
        {
            finish();
        }
        else if(view.getId() == R.id.header_btn_favorito)
        {
            Paradero found = Paradero.findByStopID(paradero.stopID);
            if(found == null)
            {
                paradero.save();
                btnFavorito.setBackgroundResource(R.drawable.btn_desguardar);
            }
            else
            {
                Paradero.deleteAll(Paradero.class, "stopID = ?", paradero.stopID);
                btnFavorito.setBackgroundResource(R.drawable.btn_guardar);
            }

            Log.d("iTransantiago", "favorito");
        }
    }

    private void load()
    {

        Utils.trackEvent(this, "VIEW", "paradero", paradero.code);

        String url = String
                .format("%s&paradero=%s", Config.URL_PREDICCION, paradero.code);

        final ProgressDialog dialog = ProgressDialog.show(this,
                "iTransantiago", "Cargando");

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject servicios = response.getJSONObject("servicios");
                    Object item = servicios.get("item");

                    JSONObject[] aux;
                    if(item instanceof JSONObject)
                    {
                        aux = new JSONObject[] { (JSONObject) item };
                    }
                    else
                    {
                        JSONArray items = response.getJSONObject("servicios").getJSONArray("item");
                        aux = new JSONObject[items.length()];
                        for(int i = 0; i < items.length(); i++)
                        {
                            aux[i] = items.getJSONObject(i);
                        }
                    }

                    final JSONObject[] values = aux;

                    listView.setAdapter(new RecorridosParaderoAdapter(RecorridosParaderoActivity.this, values));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {

                            View container = view.findViewById(R.id.recorridos_paradero_container);
                            View containerRight = view.findViewById(R.id.recorridos_paradero_container_right);

                            JSONObject item = values[position];
                            if(item.isNull("horaprediccionbus2"))
                                return;

                            if(containerRight.getVisibility() == View.GONE){
                                containerRight.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                containerRight.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("iTransantiago", error.getMessage());
                dialog.dismiss();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }


    @Override
    public void onRefresh() {

        load();

    }
}
