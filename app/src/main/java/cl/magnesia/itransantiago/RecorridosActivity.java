package cl.magnesia.itransantiago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


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

        Log.d("iTransantiago", "click");
        if (true)
        {

            Log.d("iTransantiago", textServicio.getText().toString());
            Intent intent = new Intent(this, RecorridosResultadoActivity.class);
            intent.putExtra("SERVICIO", textServicio.getText().toString());
            startActivity(intent);

        }
    }


}
