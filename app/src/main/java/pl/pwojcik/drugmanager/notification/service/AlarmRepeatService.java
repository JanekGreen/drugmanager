package pl.pwojcik.drugmanager.notification.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import java.security.cert.CertPathBuilderSpi;
import java.util.Calendar;

import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.utils.Constants;

public class AlarmRepeatService extends Service {

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            int delay = extras.getInt("DELAY", 5);
            int requestCode = extras.getInt("REQUEST_CODE", 5);
            AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext());
            alarmHelper.setAlarmForTimeWithDelayInMinutes(delay,requestCode);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                stopSelf();
                return START_NOT_STICKY;
            }

            notificationManager.cancel(Constants.INTENT_REQUEST_CODE);

            Intent ringtonePlayingIntent = new Intent(this, RingtonePlayingService.class);
            stopService(ringtonePlayingIntent);

            Toast.makeText(getApplicationContext(),"Ustawiono przypomnienie za 5 minut",Toast.LENGTH_SHORT).show();
        }
        stopSelf();

        return START_NOT_STICKY;
    }

}