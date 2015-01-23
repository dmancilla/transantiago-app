package cl.magnesia.itransantiago.models;

public class Paradero {

	public String nombre;
	public double lat, lon;

	public Paradero(String nombre, double lat, double lon) {
		this.nombre = nombre;
		this.lat = lat;
		this.lon = lon;
	}

}
