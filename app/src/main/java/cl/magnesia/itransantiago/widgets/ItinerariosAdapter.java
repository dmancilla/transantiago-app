package cl.magnesia.itransantiago.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

public class ItinerariosAdapter extends ArrayAdapter<JSONObject> {

    private Context context;
    private JSONObject[] values;
    private int selected;

    public ItinerariosAdapter(Context context, JSONObject[] values, int selected) {
        super(context, R.layout.row_itinerario, values);
        this.context = context;
        this.values = values;
        this.selected = selected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_itinerario, parent, false);

        JSONObject itinerario = values[position];

        TextView textView = (TextView) rowView.findViewById(R.id.planificador_itinerario_idx);
        TextView textDuracion = (TextView) rowView.findViewById(R.id.planificador_itinerario_duracion);

        ImageView[] imageViewLegs = new ImageView[5];
        imageViewLegs[0] = (ImageView) rowView.findViewById(R.id.planificador_itinerario_leg_1);
        imageViewLegs[1] = (ImageView) rowView.findViewById(R.id.planificador_itinerario_leg_2);
        imageViewLegs[2] = (ImageView) rowView.findViewById(R.id.planificador_itinerario_leg_3);
        imageViewLegs[3] = (ImageView) rowView.findViewById(R.id.planificador_itinerario_leg_4);
        imageViewLegs[4] = (ImageView) rowView.findViewById(R.id.planificador_itinerario_leg_5);

        textView.setText(String.format("%d", position + 1));
        textView.setBackground(selected == position ? Utils.BG_ITINERARIO_GREEN : Utils.BG_ITINERARIO_BLACK);
        try {
            textDuracion.setText(String.format("%d mins", Math.round(itinerario.getLong("duration") / (1000 * 60))));

            JSONArray legs = itinerario.getJSONArray("legs");
            for(int i = 0; i < 5; i++)
            {

                if(i < legs.length())
                {
                    JSONObject leg = legs.getJSONObject(i);
                    Drawable drawable = null;
                    if(leg.getString("mode").equals("WALK"))
                        drawable = Utils.ICO_WALK;

                    if(leg.getString("mode").equals("BUS"))
                        drawable = Utils.ICO_BUS;

                    if(leg.getString("mode").equals("SUBWAY"))
                        drawable = Utils.ICO_SUBWAY;

                    imageViewLegs[i].setVisibility(View.VISIBLE);
                    imageViewLegs[i].setBackground(drawable);
                }
                else
                {
                    imageViewLegs[i].setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }
}
