package pl.pwojcik.drugmanager.notification.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import java.io.IOException;

public class RingtonePlayingService extends Service
{
    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getBaseContext(),ringtoneUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer.stop();

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mediaPlayer.stop();
    }
}