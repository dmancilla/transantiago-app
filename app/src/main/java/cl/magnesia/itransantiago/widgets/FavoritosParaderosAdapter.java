package cl.magnesia.itransantiago.widgets;

import android.content.Context;
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
import cl.magnesia.itransantiago.models.Paradero;

public class FavoritosParaderosAdapter extends ArrayAdapter<Paradero> {

    private Context context;
    private Paradero[] paraderos;


    public FavoritosParaderosAdapter(Context context, Paradero[] paraderos) {
        super(context, R.layout.row_itinerario, paraderos);
        this.context = context;
        this.paraderos = paraderos;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_favorito_paradero, parent, false);

        Paradero paradero = paraderos[position];

        TextView textCodigo = (TextView) rowView.findViewById(R.id.favorito_paradero_codigo);
        TextView textNombre = (TextView) rowView.findViewById(R.id.favorito_paradero_nombre);

       textCodigo.setText(paradero.code);
       textNombre.setText(paradero.name);

        return rowView;
    }
}
