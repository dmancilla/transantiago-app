package cl.magnesia.itransantiago;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
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
						cl.magnesia.itransantiago.HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1500);
	}
}
