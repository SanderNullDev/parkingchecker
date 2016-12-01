package nl.blackcatapps.pchecker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sander on 01-12-16.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static String getDate(long time) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = new Date(time);
        return format.format(d);
    }

}
