package cl.magnesia.itransantiago.misc;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ribarra on 2/13/15.
 */
public class SessionManager {

    public LatLng to;

    private static SessionManager theInstance;

    public static SessionManager getInstance()
    {
        if(null == theInstance)
        {
            theInstance = new SessionManager();
        }
        return theInstance;
    }
}
