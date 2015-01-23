package cl.magnesia.itransantiago;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.app.Application;
import android.util.Log;

import static cl.magnesia.itransantiago.Config.TAG;

public class iTransantiagoApplication extends Application {

	public void onCreate() {

		super.onCreate();

		Log.d(TAG, "Application");
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
				.setFontAttrId(R.attr.fontPath).build());

	}

}
