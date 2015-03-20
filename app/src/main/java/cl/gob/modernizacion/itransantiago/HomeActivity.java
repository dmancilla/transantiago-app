package cl.gob.modernizacion.itransantiago;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import cl.gob.modernizacion.itransantiago.R;
import cl.gob.modernizacion.itransantiago.misc.MyLocationListener;

public class HomeActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_home);
		final TabHost tabHost = getTabHost();

		TabSpec planificador = tabHost.newTabSpec("Planificador");
		planificador.setIndicator("",
				getResources().getDrawable(R.drawable.tab_planificador));
		Intent planificadorIntent = new Intent(this, PlanificadorActivity.class);
		planificador.setContent(planificadorIntent);

		TabSpec paraderos = tabHost.newTabSpec("Paraderos");
		paraderos.setIndicator("",
				getResources().getDrawable(R.drawable.tab_paraderos));
		Intent paraderosIntent = new Intent(this, ParaderosActivity.class);
		paraderos.setContent(paraderosIntent);

		TabSpec favoritos = tabHost.newTabSpec("Favoritos");
		favoritos.setIndicator("",
				getResources().getDrawable(R.drawable.tab_favoritos));
		Intent favoritosIntent = new Intent(this, FavoritosActivity.class);
		favoritos.setContent(favoritosIntent);

		TabSpec recorridos = tabHost.newTabSpec("Recorridos");
		recorridos.setIndicator("",
				getResources().getDrawable(R.drawable.tab_recorridos));
		Intent recorridosIntent = new Intent(this, RecorridosActivity.class);
		recorridos.setContent(recorridosIntent);

		TabSpec puntosBIP = tabHost.newTabSpec("Puntos BIP");
		puntosBIP.setIndicator("",
				getResources().getDrawable(R.drawable.tab_bip));
		Intent puntosBIPIntent = new Intent(this, PuntosBIPActivity.class);
		puntosBIP.setContent(puntosBIPIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(planificador);
		tabHost.addTab(paraderos);
		tabHost.addTab(favoritos);
		tabHost.addTab(recorridos);
		tabHost.addTab(puntosBIP);

		// setup view
        final TabWidget tabWidget = (TabWidget) tabHost
				.findViewById(android.R.id.tabs);
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			View tabView = tabWidget.getChildTabViewAt(i);
            tabView.setId(i);
            tabView.setBackgroundColor(getResources().getColor(R.color.green_tab_off));
            TextView textView = (TextView) tabView
					.findViewById(android.R.id.title);
			textView.setTextSize(6);
			textView.setPadding(0, 0, 0, 0);

		}

        tabHost.getCurrentTabView().setBackgroundColor(getResources().getColor(R.color.green_tab_on));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabID) {


                for (int i = 0; i < tabWidget.getChildCount(); i++) {
                    tabWidget.getChildTabViewAt(i).setBackgroundColor(getResources().getColor(R.color.green_tab_off));
                }
                tabHost.getCurrentTabView().setBackgroundColor(getResources().getColor(R.color.green_tab_on));
            }
        });

        // setup GPS
        MyLocationListener.init(getApplicationContext());


	}


}
