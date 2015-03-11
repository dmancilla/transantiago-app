package cl.gob.modernizacion.itransantiago.models;

import com.orm.SugarRecord;

import java.util.Iterator;

public class Viaje extends SugarRecord<Viaje> {

    public String origen;
    public String destino;

    public Viaje()
    {

    }

    public Viaje(String origen, String destino)
    {
        this.origen = origen;
        this.destino = destino;

    }

    public static Viaje[] all()
    {
        int count = (int)Viaje.count(Viaje.class, "", null);
        Iterator<Viaje> iterator = Viaje.findAll(Viaje.class);

        Viaje[] viajes = new Viaje[count];
        int i = 0;
        while(iterator.hasNext())
        {
            viajes[i++] = iterator.next();
        }

        return viajes;

    }
}
