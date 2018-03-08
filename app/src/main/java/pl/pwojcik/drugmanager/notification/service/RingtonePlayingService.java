package pl.pwojcik.drugmanager.notification.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import java.io.IOException;

public class RingtonePlayingService extends Service
{
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),ringtoneUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        System.out.println("On destroy called");
        mediaPlayer.stop();
    }
}