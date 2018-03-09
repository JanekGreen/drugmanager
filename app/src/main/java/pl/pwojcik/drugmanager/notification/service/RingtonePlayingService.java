package pl.pwojcik.drugmanager.notification.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;

import pwojcik.pl.archcomponentstestproject.R;

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
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            boolean stopPlaying = extras.getBoolean("STOP_PLAYING", false);
            if (stopPlaying){
                stopSelf();
            }
        }else {
            mediaPlayer = MediaPlayer.create(getBaseContext(),R.raw.notification_sound2);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mediaPlayer.stop();
    }
}