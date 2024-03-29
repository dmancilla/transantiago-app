package cl.gob.modernizacion.itransantiago;

import cl.gob.modernizacion.itransantiago.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Utils.load(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				Intent intent = new Intent(MainActivity.this,
						HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1500);
	}
}
