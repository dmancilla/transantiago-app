package cl.magnesia.itransantiago;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;

import cl.magnesia.itransantiago.R;
import cl.magnesia.itransantiago.models.Paradero;

public class FavoritosActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        TextView textView = ( TextView) findViewById(R.id.favoritos_text);

        String text = "";
        Iterator<Paradero> paraderos = Paradero.findAll(Paradero.class);
        while(paraderos.hasNext())
        {
            Paradero paradero = paraderos.next();
            text += paradero.name + "\n";
        }
        textView.setText(text);
    }

}
