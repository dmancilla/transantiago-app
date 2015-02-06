package cl.magnesia.itransantiago;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;

import cl.magnesia.itransantiago.models.Paradero;
import cl.magnesia.itransantiago.models.Viaje;
import cl.magnesia.itransantiago.widgets.FavoritosParaderoAdapter;
import cl.magnesia.itransantiago.widgets.FavoritosRutaAdapter;

public class FavoritosActivity extends BaseActivity {

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

        listView = (ListView) findViewById(R.id.favoritos_list_view);
    }

    public void onClick(View view)
    {
        Log.d("iTransantiago", "ID. " + view.getId());
        if(view.getId() == R.id.favoritos_btn_rutas)
        {

            FavoritosRutaAdapter adapter = new FavoritosRutaAdapter(this, Viaje.all());
            listView.setAdapter(adapter);
        }
        else if(view.getId() == R.id.favoritos_btn_paraderos)
        {

            FavoritosParaderoAdapter adapter = new FavoritosParaderoAdapter(this, Paradero.all());
            listView.setAdapter(adapter);
        }
    }

}
