package cl.gob.modernizacion.itransantiago;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.misc.SessionManager;
import cl.gob.modernizacion.itransantiago.models.Paradero;
import cl.gob.modernizacion.itransantiago.models.Viaje;
import cl.gob.modernizacion.itransantiago.widgets.FavoritosParaderoAdapter;
import cl.gob.modernizacion.itransantiago.widgets.FavoritosRutaAdapter;

public class FavoritosActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private Viaje[] viajes;
    private Paradero[] paraderos;

    public Button buttonRutas;
    public Button buttonParaderos;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        // header
        //Button button = (Button) findViewById(R.id.header_btn_back);
        //button.setVisibility(View.VISIBLE);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText("Favoritos");

        buttonRutas = (Button) findViewById(R.id.favoritos_btn_rutas);
        buttonParaderos = (Button) findViewById(R.id.favoritos_btn_paraderos);

        listView = (ListView) findViewById(R.id.favoritos_list_view);
        listView.setOnItemClickListener(this);

        viajes = Viaje.all();
        update();

        Utils.trackScreen(this, "favoritos");

    }

    public void update()
    {
        if(viajes != null)
        {

            listView.setAdapter(new FavoritosRutaAdapter(this, viajes));
            buttonRutas.setBackgroundColor(getResources().getColor(R.color.green_favorite_on));
            buttonParaderos.setBackgroundColor(getResources().getColor(R.color.green_favorite_off));
        }
        else if(paraderos != null)
        {
            paraderos = Paradero.all();


            listView.setAdapter(new FavoritosParaderoAdapter(this, paraderos));
            buttonParaderos.setBackgroundColor(getResources().getColor(R.color.green_favorite_on));
            buttonRutas.setBackgroundColor(getResources().getColor(R.color.green_favorite_off));
        }
    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.favoritos_btn_rutas)
        {

            viajes = Viaje.all();
            paraderos = null;
            update();

        }
        else if(view.getId() == R.id.favoritos_btn_paraderos)
        {

            paraderos = Paradero.all();
            viajes = null;
            update();

        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(null != paraderos)
        {
            Paradero paradero = paraderos[position];

            Intent intent = new Intent(this, RecorridosParaderoActivity.class);
            intent.putExtra(Config.BUNDLE_PARADERO, paradero);

            startActivityForResult(intent, Config.ACTIVITY_PLANIFICADOR_TRAMO);
        }

        if(null != viajes)
        {
            Viaje viaje = viajes[position];

            Geocoder geocoder = new Geocoder(this);
            List<Address> lstTo = null;
            try {
                lstTo = geocoder.getFromLocationName(viaje.destino,
                        1, Config.lowerLeft.latitude, Config.lowerLeft.longitude,
                        Config.upperRight.latitude, Config.upperRight.longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

            LatLng latLngTo = new LatLng(lstTo.get(0).getLatitude(), lstTo.get(0).getLongitude());

            SessionManager.getInstance().to = latLngTo;

            Intent intent = new Intent(this, PlanificadorActivity.class);
            startActivity(intent);

        }
    }

    public void onResume()
    {
        super.onResume();


    }
}
