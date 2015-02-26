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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        JSONObject item = values[position];



        View row = null;

        try {

            if(item.isNull("distanciabus1"))
            {
                row = inflater.inflate(R.layout.row_paradero_fuera_servicio, parent, false);

                String servicio = item.getString("servicio");
                TextView textViewServicio = (TextView) row.findViewById(R.id.recorridos_paradero_servicio);
                textViewServicio.setText(servicio);
            }
            else {


                row = inflater.inflate(R.layout.row_paradero, parent, false);
                String servicio = item.getString("servicio");
                TextView textViewServicio = (TextView) row.findViewById(R.id.recorridos_paradero_servicio);
                textViewServicio.setText(servicio);


                String tiempo = item.getString("horaprediccionbus1");
                TextView textViewTiempo = (TextView) row.findViewById(R.id.recorridos_paradero_tiempo);
                textViewTiempo.setText(tiempo);

                int distancia = item.getInt("distanciabus1");
                TextView textViewDistancia = (TextView) row.findViewById(R.id.recorridos_paradero_distancia);
                textViewDistancia.setText(String.format("%d mts.", distancia));

                if (item.isNull("horaprediccionbus2")) {

                    row.findViewById(R.id.recorridos_paradero_siguiente).setVisibility(View.INVISIBLE);

                }
                else
                {

                    String tiempo2 = item.getString("horaprediccionbus2");
                    TextView textViewTiempo2 = (TextView) row.findViewById(R.id.recorridos_paradero_tiempo_2);
                    textViewTiempo2.setText(tiempo2);

                    int distancia2 = item.getInt("distanciabus2");
                    TextView textViewDistancia2 = (TextView) row.findViewById(R.id.recorridos_paradero_distancia_2);
                    textViewDistancia2.setText(String.format("%d mts.", distancia2));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return row;
    }
}
