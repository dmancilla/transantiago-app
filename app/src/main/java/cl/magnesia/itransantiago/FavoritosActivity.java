package cl.magnesia.itransantiago;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import cl.magnesia.itransantiago.R;
import cl.magnesia.itransantiago.models.Paradero;
import cl.magnesia.itransantiago.widgets.FavoritosParaderosAdapter;
import cl.magnesia.itransantiago.widgets.ItinerariosAdapter;

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
        if(view.getId() == R.id.favoritos_btn_paraderos)
        {

            Iterator<Paradero> paraderos = Paradero.findAll(Paradero.class);

            FavoritosParaderosAdapter adapter = new FavoritosParaderosAdapter(this, Paradero.all());
            listView.setAdapter(adapter);
        }
    }

}
