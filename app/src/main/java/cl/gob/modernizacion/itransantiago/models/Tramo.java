package cl.gob.modernizacion.itransantiago.models;

import java.io.Serializable;

/**
 * Created by ribarra on 1/25/15.
 */
public class Tramo implements Serializable {

    public Tramo(String mode, String title, String from, String fromCode, String to, String toCode, long duration, long distance)
    {
        this.mode = mode;
        this.title = title;

        this.from = from;
        this.fromCode = fromCode;

        this.to = to;
        this.toCode = toCode;

        this.duration = duration;
        this.distance = distance;
    }

    public String mode;
    public String title;

    public String from;
    public String fromCode;

    public String to;
    public String toCode;
    public long duration;
    public long distance;

}
