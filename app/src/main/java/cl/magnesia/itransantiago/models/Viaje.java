package cl.magnesia.itransantiago.models;

import com.orm.SugarRecord;

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
}
