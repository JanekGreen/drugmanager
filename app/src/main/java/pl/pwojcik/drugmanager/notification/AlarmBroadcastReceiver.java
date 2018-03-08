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
    boolean triggered = false;

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
                .setContentTitle("Przypomnienie")
                .setContentText("Pora na leki")
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setWhen(System.currentTimeMillis())
                //.setLargeIcon(Bi.createWithResource(context, R.drawable.ic_info_black_24dp))
                //.addAction(action)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(Constants.INTENT_REQUEST_CODE, notification);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("on receive " + triggered);
        Bundle extras = intent.getExtras();
        int requestCode;
        if (extras != null) {
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

          /* Intent ringtonePlayingIntent = new Intent(context, RingtonePlayingService.class);
            context.startService(ringtonePlayingIntent);*/

        }
            /*  Intent newActivityIntent = new Intent(context, NotificationActivity.class);
            newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newActivityIntent.putExtra("REQUEST_CODE", requestCode);
            context.startActivity(newActivityIntent);*/
    }


}
