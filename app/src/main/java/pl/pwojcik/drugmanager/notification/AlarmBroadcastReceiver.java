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

import pl.pwojcik.drugmanager.notification.service.RingtonePlayingService;
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

        Intent intent = new Intent(context, DrugListActivity.class);
        intent.putExtra("REQUEST_CODE",requestCode);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat
                .Action.Builder(R.mipmap.ic_launcher,
                "Szczegóły", pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        Notification notification = new NotificationCompat.Builder(context,"channel-id")
                .setContentTitle("Przypomnienie")
                .setContentText("Pora na leki")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                //.setLargeIcon(Bi.createWithResource(context, R.drawable.ic_info_black_24dp))
                .addAction(action)

                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        context.startService(startIntent);

        notificationManager.notify(Constants.INTENT_REQUEST_CODE, notification);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int requestCode;
        if(extras!=null) {
            if (context.getSystemService(Context.POWER_SERVICE) != null) {
                PowerManager.WakeLock wakeLock = ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "MyActivity");
                wakeLock.acquire(10*60*1000L /*10 minutes*/);
            }
            requestCode = extras.getInt("REQUEST_CODE");
            System.out.println("REQUEST CODE "+requestCode);
            //sendNotification(context,requestCode);
            Intent newActivityIntent = new Intent(context, NotificationActivity.class);
            newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            newActivityIntent.putExtra("REQUEST_CODE",requestCode);
            context.startActivity(newActivityIntent);

        }
    }


}
