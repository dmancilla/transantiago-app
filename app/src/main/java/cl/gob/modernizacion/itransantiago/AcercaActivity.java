package cl.gob.modernizacion.itransantiago;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cl.gob.modernizacion.itransantiago.R;

public class AcercaActivity extends Activity {

	private TextView textViewDescripcion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acerca);

		textViewDescripcion = (TextView) findViewById(R.id.acerca_descripcion);
		textViewDescripcion.setText(Html
				.fromHtml(getString(R.string.acerca_descripcion)));
	}
}
