package pl.pwojcik.drugmanager.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;

import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.notification.service.RingtonePlayingService;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;
import pl.pwojcik.drugmanager.retrofit.DrugRestService;
import pl.pwojcik.drugmanager.ui.druglist.DrugListActivity;
import pl.pwojcik.drugmanager.ui.druglist.NotificationActivity;
import pl.pwojcik.drugmanager.utils.Constants;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 14.02.18.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public void sendNotification(Context context, int requestCode) {
        System.out.println("Send notification...");

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putExtra("REQUEST_CODE", requestCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

       /* NotificationCompat.Action action = new NotificationCompat
                .Action.Builder(R.mipmap.ic_launcher,
                "Szczegóły", pendingIntent)
                .build();*/

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        Notification notification = new NotificationCompat.Builder(context, "channel-id")
                .setContentTitle("Pora na leki")
                .setContentText("Pora na leki2")
                .setDefaults(NotificationCompat.FLAG_SHOW_LIGHTS)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Pora na leki 2"))
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setWhen(System.currentTimeMillis())
                //.setLargeIcon(Bi.createWithResource(context, R.drawable.ic_info_black_24dp))
                //.addAction(action)
                .build();
        notification.flags =notification.defaults |Notification.FLAG_INSISTENT;
        notification.sound = Uri.parse("android.resource://"+context.getPackageName()+"/notification_sound.vaw");
        notificationManager.notify(Constants.INTENT_REQUEST_CODE, notification);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int requestCode;
        if (extras != null) {
           /* Intent ringtonePlayingIntent = new Intent(context, RingtonePlayingService.class);
            context.startService(ringtonePlayingIntent);*/

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
           PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "WakeLock");
            wakeLock.acquire(5000);
            requestCode = extras.getInt("REQUEST_CODE");
            DrugRepostioryImpl drugListRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
                    DrugmanagerApplication.getDbInstance(context).getDrugTimeDao(), DrugmanagerApplication.getDbInstance(context).getDrugDbDao(),
                    DrugmanagerApplication.getDbInstance(context).getDefinedTimesDao());
            drugListRepository
                    .getDefinedTimes()
                    .flatMap(list -> io.reactivex.Observable.fromIterable(list)
                            .filter(definedTime -> definedTime.getRequestCode() == requestCode)
                            .singleElement())
                    .doOnSuccess(definedTime -> System.out.println("Defined Time time " + definedTime.getTime()))
                    .subscribe(definedTime -> {
                        AlarmHelper alarmHelper = new AlarmHelper(context);
                        int hour, minute;
                        String hourMinuteParts[] = definedTime.getTime().split(":");
                        hour = Integer.valueOf(hourMinuteParts[0]);
                        minute = Integer.valueOf(hourMinuteParts[1]);
                        alarmHelper.setAlarmForTimeRepeating(hour, minute, 1, definedTime.getRequestCode(), 0, true);
                    });

            sendNotification(context, requestCode);

        }
            /*  Intent newActivityIntent = new Intent(context, NotificationActivity.class);
            newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newActivityIntent.putExtra("REQUEST_CODE", requestCode);
            context.startActivity(newActivityIntent);*/
    }


}
