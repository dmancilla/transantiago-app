package cl.gob.modernizacion.itransantiago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cl.gob.modernizacion.itransantiago.R;
import cl.gob.modernizacion.itransantiago.models.Tramo;


public class PlanificadorTramoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_planificador_tramo);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        Tramo tramo = (Tramo)bundle.getSerializable("TRAMO");

        View view;
        TextView textView;

        // header
        view = (View)findViewById(R.id.header);
        Button button = (Button) view.findViewById(R.id.header_btn_back);
        button.setVisibility(View.VISIBLE);

        textView = (TextView)view.findViewById(R.id.header_titulo);
        textView.setText("Tramo");

        // Información del tramo
        view = (View)findViewById(R.id.planificador_tramo_row_1);
        textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
        textView.setText("Tramo");
        textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
        textView.setText(tramo.title);

        view = (View)findViewById(R.id.planificador_tramo_row_2);
        textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
        textView.setText("Duración");
        textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
        textView.setText(String.format("%d minutos", tramo.duration / (10000 * 60)));

        view = (View)findViewById(R.id.planificador_tramo_row_3);
        textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
        textView.setText("Distancia");
        textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
        textView.setText(String.format("%.1f kilómetros", tramo.distance * 1.0f / (1000 * 10)));

        // Desde
        view = (View)findViewById(R.id.planificador_tramo_desde);
        textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
        textView.setText(tramo.from);

        if(tramo.mode.equals(("BUS")))
        {
            view = (View)findViewById(R.id.planificador_tramo_desde_codigo);
            view.setVisibility(View.VISIBLE);
            textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
            textView.setText("Código");
            textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
            textView.setText(tramo.fromCode);

            view = (View)findViewById(R.id.planificador_tramo_desde_ver);
            view.setVisibility(View.VISIBLE);
            textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
            textView.setText("");
            textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
            textView.setText("Ver buses cercanos");
        }

        // Hasta
        view = (View)findViewById(R.id.planificador_tramo_hasta);
        textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
        textView.setText(tramo.from);

        if(tramo.mode.equals(("BUS")))
        {
            view = (View)findViewById(R.id.planificador_tramo_hasta_codigo);
            view.setVisibility(View.VISIBLE);
            textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
            textView.setText("Código");
            textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
            textView.setText(tramo.toCode);

            view = (View)findViewById(R.id.planificador_tramo_hasta_ver);
            view.setVisibility(View.VISIBLE);
            textView = (TextView) view.findViewById(R.id.planificador_tramo_nombre);
            textView.setText("");
            textView = (TextView) view.findViewById(R.id.planificador_tramo_valor);
            textView.setText("Ver buses cercanos");
        }

        Utils.trackScreen(this, "planificador-tramo");

    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.header_btn_back) {
            finish();
        }
    }
}
