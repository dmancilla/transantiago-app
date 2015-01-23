package cl.magnesia.itransantiago;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cl.magnesia.itransantiago.models.Paradero;
import cl.magnesia.itransantiago.models.Ruta;
import cl.magnesia.itransantiago.models.Trip;

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

        String[] sqlSelect = { "stop_name", "stop_lat", "stop_lon" };
        String sqlTables = "stops";

        qb.setTables(sqlTables);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);
        cursor.moveToFirst();

        List<Paradero> paraderos = new ArrayList<Paradero>();
        while (cursor.moveToNext()) {

            String nombre = cursor.getString(0);
            double lat = cursor.getDouble(1);
            double lon = cursor.getDouble(2);

            Paradero paradero = new Paradero(nombre, lat, lon);
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


}
