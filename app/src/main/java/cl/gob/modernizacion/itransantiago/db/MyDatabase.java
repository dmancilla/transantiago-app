package cl.gob.modernizacion.itransantiago.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cl.gob.modernizacion.itransantiago.models.Paradero;
import cl.gob.modernizacion.itransantiago.models.PuntoBIP;
import cl.gob.modernizacion.itransantiago.models.Ruta;
import cl.gob.modernizacion.itransantiago.models.Trip;

import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "itransantiago.sqlite";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<Paradero> getParaderos() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = { "stop_id", "stop_code", "stop_name", "stop_lat", "stop_lon" };
        String sqlTables = "stops";

        qb.setTables(sqlTables);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);
        cursor.moveToFirst();

        List<Paradero> paraderos = new ArrayList<Paradero>();
        while (cursor.moveToNext()) {

            String stopID = cursor.getString(0);
            String code = cursor.getString(1);
            String name = cursor.getString(2);
            double lat = cursor.getDouble(3);
            double lon = cursor.getDouble(4);

            Paradero paradero = new Paradero(stopID, code, name, lat, lon);
            paraderos.add(paradero);

        }
        return paraderos;

    }

    public Ruta getRuta(String servicio)
    {
        // CREATE TABLE "routes" ("route_id" VARCHAR PRIMARY KEY  NOT NULL ,"agency_id" VARCHAR,"route_short_name" VARCHAR,"route_long_name" VARCHAR,"route_desc" VARCHAR,"route_type" INTEGER,"route_url" VARCHAR,"route_color" VARCHAR,"route_text_color" VARCHAR);
        SQLiteDatabase db = getReadableDatabase();

        Log.d("iTransantiago", "cursor");
        Log.d("iTransantiago", servicio);

        Cursor cursor = db.rawQuery("SELECT route_id, route_type FROM routes WHERE route_id = ?", new String[] { servicio});
        cursor.moveToFirst();

        Ruta ruta = new Ruta();
        try {
            ruta.routeId = cursor.getString(0);
            ruta.routeType = cursor.getInt(1);
        }
        catch(Exception exception)
        {
            return null;
        }

        return ruta;
    }

    public Trip getTripByRouteId(String routeId, int direction)
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String serviceID;
        if(day == 1)
            serviceID = "D_V9";
        else if(day == 7)
            serviceID = "S_V9";
        else
            serviceID = "L_V9";

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        String currentTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        Log.d("iTransantiago", currentTime);

        SQLiteDatabase db = getReadableDatabase();

        Log.d("iTransantiago", "cursor");
        Log.d("iTransantiago", routeId);
        Log.d("iTransantiago", serviceID);

        Cursor cursor = db.rawQuery("SELECT trips.trip_id, trips.trip_headsign " +
                        "FROM trips " +
                        "LEFT JOIN frequencies ON trips.trip_id = frequencies.trip_id " +
                        "WHERE trips.route_id = ? COLLATE NOCASE AND trips.direction_id = ? AND trips.service_id = ? AND frequencies.start_time <= ? AND frequencies.end_time >= ?",
                new String[] { routeId, String.format("%d", direction), serviceID, currentTime, currentTime });


        while(cursor.moveToNext())
        {

            Trip trip = new Trip();
            trip.tripID = cursor.getString(0);
            trip.tripHeadSign = cursor.getString(1);

            Log.d("iTransantiago", "tripID " + trip.tripID + ", " + trip.tripHeadSign);

            return trip;
        }

        return null;

    }

    public List<Paradero> getParaderosByTripId(String tripID)
    {

        SQLiteDatabase db = getReadableDatabase();



        Cursor cursor = db.rawQuery("SELECT stops.stop_id, stops.stop_code, stops.stop_name, stops.stop_lat, stops.stop_lon " +
                        "FROM stop_times " +
                        "LEFT JOIN trips ON stop_times.trip_id=trips.trip_id " +
                        "LEFT JOIN stops ON stop_times.stop_id=stops.stop_id " +
                        "WHERE trips.trip_id = ? " +
                        "ORDER BY stop_times.stop_sequence",
                new String[] { tripID });

        List<Paradero> paraderos = new ArrayList<Paradero>();
        while(cursor.moveToNext())
        {
            String stopID = cursor.getString(0);
            String code = cursor.getString(1);
            String name = cursor.getString(2);
            double lat = cursor.getDouble(3);
            double lng = cursor.getDouble(4);

            Paradero paradero = new Paradero(stopID, code, name, lat, lng);
            paraderos.add(paradero);
        }

        return paraderos;
    }

    public Paradero findParaderoByID(String ID)
    {

        SQLiteDatabase db = getReadableDatabase();



        Cursor cursor = db.rawQuery("SELECT stops.stop_id, stops.stop_code, stops.stop_name, stops.stop_lat, stops.stop_lon " +
                        "FROM stops " +
                "WHERE stop_id = ?",
                new String[] { ID });

        if(cursor.moveToNext())
        {
            String stopID = cursor.getString(0);
            String code = cursor.getString(1);
            String name = cursor.getString(2);
            double lat = cursor.getDouble(3);
            double lng = cursor.getDouble(4);

            Paradero paradero = new Paradero(stopID, code, name, lat, lng);
            return paradero;
        }
        else {
            return null;
        }
    }

    public List<LatLng> getShapeByTripId(String tripId)
    {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT shapes.shape_pt_lat, shapes.shape_pt_lon "+
                "FROM trips "+
                "LEFT JOIN shapes ON trips.shape_id = shapes.shape_id "+
                "WHERE trips.trip_id = ? "+
                "ORDER BY shapes.shape_pt_sequence", new String[] { "" + tripId });

        ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
        while(cursor.moveToNext())
        {
            double lat = cursor.getDouble(0);
            double lon = cursor.getDouble(1);

            LatLng latLng = new LatLng(lat, lon);
            latLngs.add(latLng);
        }

        Log.d("iTransantiago", "size. " + latLngs.size());
        return latLngs;

    }

    public List<PuntoBIP> getPuntosBIP()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT entidad, direccion, comuna, horario, lat, lon FROM puntobip", null);

        ArrayList<PuntoBIP> puntosBIP = new ArrayList<PuntoBIP>();
        while(cursor.moveToNext())
        {

            String entidad = cursor.getString(0);
            String direccion = cursor.getString(1);
            String comuna = cursor.getString(2);
            String horario = cursor.getString(3);
            double lat = cursor.getDouble(4);
            double lon = cursor.getDouble(5);

            LatLng latLng = new LatLng(lat, lon);

            PuntoBIP puntoBIP = new PuntoBIP(entidad, direccion, comuna, horario, latLng);
            puntosBIP.add(puntoBIP);

        }

        return puntosBIP;
    }


}
