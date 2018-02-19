package pl.pwojcik.drugmanager.notification.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Collection;
import java.util.List;

import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.notification.AlarmBroadcastReceiver;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.TimeUtil;

/**
 * Created by pawel on 14.02.18.
 */

public class AlarmHelper {
   private AlarmManager alarmManager;
    private Context context;

    public AlarmHelper(Context context) {
        this.context = context;
        alarmManager =(AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);

    }

    public Intent getAlarmIntent(){
        return new Intent(context, AlarmBroadcastReceiver.class);
    }

    public PendingIntent getPendingIntent(){
        return PendingIntent.getBroadcast(context, Constants.INTENT_REQUEST_CODE, getAlarmIntent(), PendingIntent.FLAG_ONE_SHOT);
    }

    public PendingIntent getPendingIntent(int requestCode, int flag){
        return PendingIntent.getBroadcast(context, requestCode, getAlarmIntent(), flag);
    }

    public void setAlarmForTimeRepeating(int hour, int minute, int second){

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, TimeUtil.getSpecificTime(hour,minute,second),
                AlarmManager.INTERVAL_DAY, getPendingIntent());
    }

    public void setAlarmForTimeRepeating(int hour, int minute, int dayInterval, int requestCode, int intentFlag){

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, TimeUtil.getSpecificTime(hour,minute),
                AlarmManager.INTERVAL_DAY*dayInterval, getPendingIntent(requestCode,intentFlag));
    }

    public boolean isIntentEqual(Intent intent1, Intent intent2){
        return  intent1.filterEquals(intent2);
    }

    public void cancelAlarm(){
        alarmManager.cancel(getPendingIntent());
    }
    public void cancelAlarm(int requestCode){
        alarmManager.cancel(getPendingIntent(requestCode,0));
    }

    public void setOrUpdateAlarms(List<DefinedTime> definedTimes){


    }

}
