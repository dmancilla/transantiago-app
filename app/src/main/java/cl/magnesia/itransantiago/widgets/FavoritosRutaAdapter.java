package cl.magnesia.itransantiago.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cl.magnesia.itransantiago.R;
import cl.magnesia.itransantiago.models.Paradero;
import cl.magnesia.itransantiago.models.Viaje;

public class FavoritosRutaAdapter extends ArrayAdapter<Viaje> {

    private Context context;
    private Viaje[] viajes;


    public FavoritosRutaAdapter(Context context, Viaje[] viajes) {
        super(context, R.layout.row_itinerario, viajes);
        this.context = context;
        this.viajes = viajes;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_favorito, parent, false);

        Viaje viaje = viajes[position];

        TextView textCodigo = (TextView) rowView.findViewById(R.id.favorito_paradero_codigo);
        TextView textNombre = (TextView) rowView.findViewById(R.id.favorito_paradero_nombre);

       textCodigo.setText(String.format("Desde: %s", null == viaje.origen ? "Ubicación actual" : viaje.origen));
       textNombre.setText(String.format("Hacia: %s", null == viaje.destino ? "Ubicación actual" : viaje.destino));

        return rowView;
    }
}
