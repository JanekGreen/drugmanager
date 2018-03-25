package pl.pwojcik.drugmanager.notification.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
       Bundle bundle =  intent.getExtras();
       if(bundle!=null && bundle.getBoolean("STOP_SOUND",false)){
           System.out.println("stop sound true");
           stopSelf();
       }else {

           mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.notification_sound2);
           mediaPlayer.start();
           mediaPlayer.setLooping(true);

           Handler handler = new Handler();
           handler.postDelayed(this::stopSelf, 1000 * 60);
       }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mediaPlayer.stop();
    }
}