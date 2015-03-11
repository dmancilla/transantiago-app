package cl.gob.modernizacion.itransantiago;

import cl.magnesia.itransantiago.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.app.Application;
import android.util.Log;

import static cl.gob.modernizacion.itransantiago.Config.TAG;

public class iTransantiagoApplication extends Application {

	public void onCreate() {

		super.onCreate();

		Log.d(TAG, "Application");
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
				.setFontAttrId(R.attr.fontPath).build());

	}

}
