package cl.gob.modernizacion.itransantiago;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.widgets.ItinerariosAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static cl.gob.modernizacion.itransantiago.Config.TAG;

public class PlanificadorItinerariosActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private TextView textView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_planificador_itinerarios);

        listView = (ListView) findViewById(R.id.planificador_itinerarios);
        listView.setOnItemClickListener(this);

        textView = (TextView)findViewById(R.id.planificador_itinerarios_rutas);


        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        JSONArray itineraries = null;
        try {


            itineraries = new JSONArray(bundle.getString("ITINERARIOS"));
            int selectedItinerary = bundle.getInt("ITINERARIO");
            JSONObject[] values = new JSONObject[itineraries.length()];
            for(int i = 0; i < itineraries.length(); i++)
            {
                values[i] = itineraries.getJSONObject(i);
            }
            ItinerariosAdapter adapter = new ItinerariosAdapter(this, values, selectedItinerary);
            listView.setAdapter(adapter);

            textView.setText(String.format("Encontramos %d rutas posibles:", itineraries.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.trackScreen(this, "planificador-itinerarios");

	}

	public void onClick(View view) {

		Log.d(TAG, "view. " + view.getClass());
		Log.d(TAG, "view. " + view.getId());

		if (view.getId() == R.id.planificador_back) {
			setResult(Config.ACTIVITY_BACK);
			finish();
		}
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // seleccionó un item, volvemos atrás y avisamos cuál seleccionó
        Intent data = new Intent();
        data.putExtra("response", position);
        setResult(Config.ACTIVITY_PLANIFICADOR_ITINERARIO, data);
        finish();
    }
}
