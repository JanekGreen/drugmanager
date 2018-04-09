package pl.pwojcik.drugmanager;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;


import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.persistence.AppDatabase;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.notification.ConnectivityChangeReceiver;

/**
 * Created by pawel on 01.02.18.
 */

public class DrugmanagerApplication extends Application {
    private static AppDatabase db;
    private static ExecutorService executorSingleThread;

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        getApplicationContext().
                registerReceiver(new ConnectivityChangeReceiver(),
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (db == null)
            getDbInstance(getApplicationContext());
    }

    public static AppDatabase getDbInstance(Context context) {
        if (db == null) {
            db = Room
                    .databaseBuilder(context, AppDatabase.class, "alarmmanagerDB")
                    .build();
        }
        return db;
    }

    public static ExecutorService getExecutorSingleThread() {
        if (executorSingleThread == null) {
            executorSingleThread = Executors.newSingleThreadExecutor();
        }

        return executorSingleThread;
    }

    private void writeToDatabaseRequiredValues() {

        db.getDefinedTimesDao()
                .getDefinedTimesCount()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(size -> {
                    if (size == 0) {
                        DefinedTime definedTime = new DefinedTime();
                        definedTime.setName("Rano");
                        definedTime.setTime("8:00");
                        definedTime.setRequestCode(0);
                        db.getDefinedTimesDao()
                                .insertDefinedTime(definedTime);
                    }
                })
                .subscribe();
    }

}
