package dev.dpvb.util;

import java.util.Calendar;

public class TimeUtil {

    public static long timeToNextHour() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);

        return (60 - minutes) * 60 * 1000 - seconds * 1000 - millis;
    }

}
