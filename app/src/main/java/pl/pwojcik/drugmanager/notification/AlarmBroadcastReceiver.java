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

import pl.pwojcik.drugmanager.ui.druglist.DrugListActivity;
import pl.pwojcik.drugmanager.utils.Constants;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 14.02.18.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public void sendNotification(Context context) {
        System.out.println("Send notification...");
        Intent intent = new Intent(context, DrugListActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,
                        1,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT);

        Notification.Action action = new Notification
                .Action.Builder(R.mipmap.ic_launcher,
                "Szczegóły", pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        Notification notification = new Notification.Builder(context)
                .setContentTitle("Przypomnienie")
                .setContentText("Pora na leki")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(Icon.createWithResource(context, R.drawable.ic_info_black_24dp))
                .setLargeIcon(Icon.createWithResource(context, R.drawable.ic_info_black_24dp))
                .addAction(action)
                .build();


        try {
            Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, ringtone);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationManager.notify(Constants.INTENT_REQUEST_CODE, notification);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }
}
