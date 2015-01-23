package cl.magnesia.itransantiago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class RecorridosActivity extends Activity {

    private TextView textServicio;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_recorridos);



        textServicio = (TextView) findViewById(R.id.recorridos_servicio);
    }

    public void onClick(View view) {

        Log.d("iTransantiago", "click");
        if (true)
        {

            Log.d("iTransantiago", textServicio.getText().toString());
            Intent intent = new Intent(this, RecorridosResultado.class);
            intent.putExtra("SERVICIO", textServicio.getText().toString());
            startActivity(intent);

        }
    }


}
