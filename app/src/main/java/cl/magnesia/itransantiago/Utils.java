package cl.magnesia.itransantiago;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Utils {

    public static Drawable ICO_WALK;
    public static Drawable ICO_BUS;
    public static Drawable ICO_SUBWAY;

    public static Drawable BG_ITINERARIO_BLACK;
    public static Drawable BG_ITINERARIO_GREEN;


    public static void load(Context context)
    {
        ICO_WALK = context.getResources().getDrawable(R.drawable.ico_walk);
        ICO_BUS = context.getResources().getDrawable(R.drawable.ico_bus);
        ICO_SUBWAY = context.getResources().getDrawable(R.drawable.ico_subway);

        BG_ITINERARIO_BLACK = context.getResources().getDrawable(R.drawable.cuadro_negro);
        BG_ITINERARIO_GREEN = context.getResources().getDrawable(R.drawable.cuadro_verde);

    }

	public static void errorDialog(Context context, String error) {
		new AlertDialog.Builder(context).setTitle("iTransantiago")
				.setMessage(error)
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				}).show();
	}

}
