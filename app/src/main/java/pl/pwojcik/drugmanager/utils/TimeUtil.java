package pl.pwojcik.drugmanager.utils;

import java.util.Calendar;
import java.util.Date;

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
    public static long getSpecificTime(int hour, int minute,int day, boolean nextDay){
        Calendar calendar = Misc.getNextDayDate(Calendar.getInstance(), day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        long diff = Calendar.getInstance().getTime().getTime() - calendar.getTime().getTime();
        System.out.println("DIFF "+ diff);
        if(diff >= 1 || nextDay) {
            calendar.add(Calendar.DATE, 1);
        }else{
            System.out.println("Not adding day");
        }

        return calendar.getTimeInMillis();
    }
    public static long getSpecificTime(int hour){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTimeInMillis();
    }
}
