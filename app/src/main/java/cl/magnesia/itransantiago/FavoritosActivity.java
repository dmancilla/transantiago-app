package cl.magnesia.itransantiago;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;

import cl.magnesia.itransantiago.models.Paradero;
import cl.magnesia.itransantiago.models.Viaje;
import cl.magnesia.itransantiago.widgets.FavoritosParaderoAdapter;
import cl.magnesia.itransantiago.widgets.FavoritosRutaAdapter;

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
    }

    public void onResume()
    {
        super.onResume();


    }
}
