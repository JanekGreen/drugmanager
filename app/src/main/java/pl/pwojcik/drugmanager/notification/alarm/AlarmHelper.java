package pl.pwojcik.drugmanager.notification.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
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

    public Intent getAlarmIntent(int requestCode){
        Intent alarmIntent = new Intent(context,AlarmBroadcastReceiver.class);
        alarmIntent.putExtra("REQUEST_CODE",requestCode);
        return alarmIntent;
    }

    public PendingIntent getPendingIntent(int requestCode, int flag){
        return PendingIntent.getBroadcast(context, requestCode, getAlarmIntent(requestCode), flag);
    }

    public void setAlarmForTimeRepeating(int hour, int minute, int dayInterval, int requestCode, int intentFlag){

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, TimeUtil.getSpecificTime(hour,minute),
                AlarmManager.INTERVAL_DAY*dayInterval, getPendingIntent(requestCode,intentFlag));
    }

    public boolean isIntentEqual(Intent intent1, Intent intent2){
        return  intent1.filterEquals(intent2);
    }

    public void cancelAlarm(int requestCode){
        alarmManager.cancel(getPendingIntent(requestCode,0));
    }

    public void cancelAllAlarms(List<DefinedTime> definedTimes){
        //definedTimes.forEach(definedTime -> alarmManager.cancel(getPendingIntent(definedTime.getRequestCode(),0)));
        Observable.fromIterable(definedTimes)
                .doOnNext(definedTime -> alarmManager.cancel(getPendingIntent(definedTime.getRequestCode(),0)))
                .subscribe();

    }

    public void setOrUpdateAlarms(List<DefinedTime> definedTimes){
        Observable.fromIterable(definedTimes)
                .doOnNext(definedTime -> {
                    int hour,minute;
                    String hourMinuteParts[] = definedTime.getTime().split(":");
                    hour =Integer.valueOf(hourMinuteParts[0]);
                    minute =Integer.valueOf(hourMinuteParts[1]);
                    setAlarmForTimeRepeating(hour,minute,1,definedTime.getRequestCode(),0);
                })
                .subscribe();

        /*
        definedTimes.forEach(definedTime -> {
            int hour,minute;
            String hourMinuteParts[] = definedTime.getTime().split(":");
            hour =Integer.valueOf(hourMinuteParts[0]);
            minute =Integer.valueOf(hourMinuteParts[1]);
            setAlarmForTimeRepeating(hour,minute,1,definedTime.getRequestCode(),0);
        });*/
    }

}
