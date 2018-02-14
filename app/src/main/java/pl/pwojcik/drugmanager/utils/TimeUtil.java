package pl.pwojcik.drugmanager.utils;

import java.util.Calendar;

/**
 * Created by pawel on 14.02.18.
 */

public class TimeUtil {

    public static long getSpecificTime(int hour, int minute, int second){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTimeInMillis();
    }
    public static long getSpecificTime(int hour, int minute){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);
        return calendar.getTimeInMillis();
    }
    public static long getSpecificTime(int hour){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTimeInMillis();
    }
}
