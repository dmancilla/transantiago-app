package cl.gob.modernizacion.itransantiago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.db.MyDatabase;
import cl.gob.modernizacion.itransantiago.models.Ruta;


public class RecorridosActivity extends BaseActivity {

    private TextView textServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recorridos);

        // header
        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText("Recorridos");


        textServicio = (TextView) findViewById(R.id.recorridos_servicio);
    }

    public void onClick(View view) {

        String servicio = textServicio.getText().toString();
        Ruta ruta = new MyDatabase(getApplicationContext()).getRuta(servicio);
        if (null == ruta) {
            Utils.errorDialog(this, "Número de recorrido no fue encontrado");
            return;
        }
        else
        {
            Intent intent = new Intent(this, RecorridosResultadoActivity.class);
            intent.putExtra(Config.BUNDLE_SERVICIO, servicio);
            startActivity(intent);

        }
    }


}
