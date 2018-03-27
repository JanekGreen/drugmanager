package pl.pwojcik.drugmanager.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDaysDao;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.notification.service.AlarmRepeatService;
import pl.pwojcik.drugmanager.notification.service.RingtonePlayingService;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;
import pl.pwojcik.drugmanager.retrofit.DrugRestService;
import pl.pwojcik.drugmanager.ui.druglist.DrugListActivity;
import pl.pwojcik.drugmanager.ui.druglist.NotificationActivity;
import pl.pwojcik.drugmanager.utils.Constants;
import pl.pwojcik.drugmanager.utils.Misc;
import pwojcik.pl.archcomponentstestproject.R;

/**
 * Created by pawel on 14.02.18.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private DrugRepostioryImpl drugListRepository;
    private DefinedTimesDaysDao definedTimesDaysDao;

    public void sendNotification(Context context, int requestCode) {
        System.out.println("Send notification...");

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        drugListRepository.getDrugsForRequestCode(requestCode)
                .map(DrugDb::getName)
                .toList()
                .subscribe(list -> {
                    String content = android.text.TextUtils.join(", ", list);
                    String title = "Leki do wzięcia: " + list.size();
                    Notification notification = createNotification(context, title, content, requestCode);
                    notification.flags |= Notification.FLAG_INSISTENT;
                    notificationManager.notify(Constants.INTENT_REQUEST_CODE, notification);
                }, e -> {
                    System.out.println(e.getMessage());
                    Notification notification = createNotification(context, "Pora na leki", "Masz do wzięcia leki", requestCode);
                    notification.flags |= Notification.FLAG_INSISTENT;
                    notificationManager.notify(Constants.INTENT_REQUEST_CODE, notification);
                });

    }

    private Notification createNotification(Context context, String title, String contentMessage, int requestCode) {

        Intent repeatIntent = new Intent(context, AlarmRepeatService.class);
        repeatIntent.putExtra("REQUEST_CODE", requestCode);
        repeatIntent.putExtra("DELAY", 5);
        PendingIntent repeatNotificationIntent = PendingIntent.getService(context, 989, repeatIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action action = new NotificationCompat
                .Action.Builder(R.drawable.ic_timer_off_black_24dp,
                "Przypomnij za 5 minut", repeatNotificationIntent)
                .build();

        /*Intent startIntent = new Intent(context, NotificationActivity.class);
        startIntent.putExtra("REQUEST_CODE", requestCode);*/

        Intent startIntent = new Intent(context, DrugListActivity.class);
        startIntent.putExtra("NOTIFICATION_OFF",true);
        startIntent.putExtra("REQUEST_CODE",requestCode);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent actionIntent = PendingIntent.getActivity(context, 1, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);


      /*  Intent soundStopIntent = new Intent(context, RingtonePlayingService.class);
        soundStopIntent.putExtra("STOP_SOUND", true);*/


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel-id")
                .setContentTitle(title)
                .setContentText(contentMessage)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(actionIntent)
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound2))
                .setDefaults(0)
                .setTicker("Powiadomienie")
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setColorized(true)
                .setWhen(System.currentTimeMillis())
                .setLights(Color.GREEN, 500, 2000)
                .addAction(action);
        //.setLargeIcon(Bi.createWithResource(context, R.drawable.ic_info_black_24dp))
        //.setDeleteIntent(PendingIntent.getService(context,0,soundStopIntent,PendingIntent.FLAG_UPDATE_CURRENT))
        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(am!=null) {
            if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                builder.setVibrate(null);
            } else {
                if (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    builder.setVibrate(new long[]{500, 500});
                }
            }
        }

        return builder.build();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int requestCode;
        if (extras != null) {
            drugListRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
                    DrugmanagerApplication.getDbInstance(context).getDrugTimeDao(), DrugmanagerApplication.getDbInstance(context).getDrugDbDao(),
                    DrugmanagerApplication.getDbInstance(context).getDefinedTimesDao(), DrugmanagerApplication.getDbInstance(context).getDefinedTimesDaysDao());

/*            Intent ringtonePlayingIntent = new Intent(context, RingtonePlayingService.class);
            context.startService(ringtonePlayingIntent);*/

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "WakeLock");
            wakeLock.acquire(1000 * 60);


            requestCode = extras.getInt("REQUEST_CODE");
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
                        definedTimesDaysDao = DrugmanagerApplication.getDbInstance(context).getDefinedTimesDaysDao();

                        definedTimesDaysDao.getDefinedTimeDaysForDefinedTime(definedTime.getId())
                                .subscribeOn(Schedulers.io())
                                .flatMap(definedTimesDays -> io.reactivex.Observable.fromIterable(definedTimesDays)
                                        .map(DefinedTimesDays::getDay)
                                        .toList()
                                        .map(Misc::getNextDay)
                                        .toMaybe())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(day -> {
                                    alarmHelper.setAlarmForTimeRepeating(hour, minute, day, definedTime.getRequestCode(), 0, false);
                                });
                    });

            sendNotification(context, requestCode);

        }
            /*Intent newActivityIntent = new Intent(context, NotificationActivity.class);
            newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newActivityIntent.putExtra("REQUEST_CODE", requestCode);
            context.startActivity(newActivityIntent);*/
    }


}
