package cl.gob.modernizacion.itransantiago;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import cl.magnesia.itransantiago.R;
import cl.gob.modernizacion.itransantiago.misc.SessionManager;
import cl.gob.modernizacion.itransantiago.models.PuntoBIP;

public class PuntosBIPDetalleActivity extends BaseFragmentActivity {

    private GoogleMap map;
    private PuntoBIP puntoBIP;

    private List<PuntoBIP> puntosBIP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos_bip_detalle);

        TextView textView = (TextView)findViewById(R.id.header_titulo);
        textView.setText("Punto BIP");

        // DATA
        puntoBIP = (PuntoBIP) getIntent().getExtras().getSerializable(Config.BUNDLE_PUNTO_BIP);


        // setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.puntos_bip_mapa);
        map = mapFragment.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.bip_annotation)).getBitmap());

        map.addMarker(new MarkerOptions()
                .title(puntoBIP.getTitle())
                .snippet(puntoBIP.getSnippet())
                .position(new LatLng(puntoBIP.lat, puntoBIP.lng))
                .icon(icon));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(puntoBIP.lat, puntoBIP.lng), 15));

        // header
        View view = (View)findViewById(R.id.header);
        Button button = (Button) view.findViewById(R.id.header_btn_back);
        button.setVisibility(View.VISIBLE);

        // UI
        ((TextView)findViewById(R.id.puntos_bip_entidad)).setText(puntoBIP.entidad);
        ((TextView)findViewById(R.id.puntos_bip_direccion)).setText(puntoBIP.direccion);
        ((TextView)findViewById(R.id.puntos_bip_comuna)).setText(puntoBIP.comuna);
        ((TextView)findViewById(R.id.puntos_bip_horario)).setText(puntoBIP.horario);

    }

    public void onClick(View view)
    {

        if(view.getId() == R.id.header_btn_back)
        {
            finish();
        }
        else if(view.getId() == R.id.puntos_bip_como_llegar) {

            SessionManager.getInstance().to = new LatLng(puntoBIP.lat, puntoBIP.lng);

            Intent intent = new Intent(this, PlanificadorActivity.class);
            startActivity(intent);

        }

    }


}
