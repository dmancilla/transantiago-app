package cl.magnesia.itransantiago.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.magnesia.itransantiago.R;
import cl.magnesia.itransantiago.Utils;

public class RecorridosParaderoAdapter extends ArrayAdapter<JSONObject> {

    private Context context;
    private JSONObject[] values;

    public RecorridosParaderoAdapter(Context context, JSONObject[] values) {
        super(context, R.layout.row_itinerario, values);
        this.context = context;
        this.values = values;
    }

    public void onClick(View view)
    {
        Log.d("iTransantiago", "click click");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_paradero, parent, false);

        JSONObject item = values[position];



        try {
            String servicio  = item.getString("servicio");
            TextView textViewServicio = (TextView)row.findViewById(R.id.recorridos_paradero_servicio);
            textViewServicio.setText(servicio);

            String tiempo = item.getString("horaprediccionbus1");
            TextView textViewTiempo = (TextView)row.findViewById(R.id.recorridos_paradero_tiempo);
            textViewTiempo.setText(tiempo);

            int distancia = item.getInt("distanciabus1");
            TextView textViewDistancia = (TextView)row.findViewById(R.id.recorridos_paradero_distancia);
            textViewDistancia.setText(String.format("%d mts.", distancia));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return row;
    }
}
