package cl.gob.modernizacion.itransantiago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.db.MyDatabase;
import cl.gob.modernizacion.itransantiago.models.Paradero;


public class ParaderosBusquedaActivity extends BaseActivity {

    private TextView textParadero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paraderos_busqueda);

        // header
        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText("Itinerarios");

        findViewById(R.id.header_btn_close).setVisibility(View.VISIBLE);


        textParadero = (TextView) findViewById(R.id.paraderos_codigo);

        trackScreen("paradero-busqueda");
    }

    public void onClick(View view) {

        if( view.getId() == R.id.header_btn_close)
        {
            finish();
        }
        else if(view.getId() == R.id.paraderos_ok)
        {

            String code = textParadero.getText().toString().toUpperCase();
            Paradero paradero = new MyDatabase(this).findParaderoByID(code);

            if(null == paradero)
            {
                Utils.errorDialog(this, "Código de paradero no fue encontrado");
            }
            else
            {
                Intent intent = new Intent(this, RecorridosParaderoActivity.class);
                intent.putExtra(Config.BUNDLE_PARADERO, paradero);
                startActivity(intent);
            }
        }
    }
}
