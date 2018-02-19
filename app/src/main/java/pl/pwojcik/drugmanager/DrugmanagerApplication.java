package pl.pwojcik.drugmanager;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.pwojcik.drugmanager.model.persistence.AppDatabase;

/**
 * Created by pawel on 01.02.18.
 */

public class DrugmanagerApplication extends Application {
    private static AppDatabase db;
    private static ExecutorService executorSingleThread;

    @Override
    public void onCreate() {
        super.onCreate();
        if(db==null)
            getDbInstance(getApplicationContext());
        /*
        DefinedTime definedTime = new DefinedTime();
        definedTime.setName("Rano");
        definedTime.setTime("8:00");
        definedTime.setRequestCode(1);

        getExecutorSingleThread().submit(()->{
            db.getDefinedTimesDao().insertDefinedTime(definedTime);
        });
        */

    }

    public static AppDatabase getDbInstance(Context context) {
        if (db == null) {
            db = Room
                    .databaseBuilder(context, AppDatabase.class, "alarmmanagerDB")
                    .build();
        }
        return db;
    }

    public static ExecutorService getExecutorSingleThread(){
        if(executorSingleThread == null){
            executorSingleThread = Executors.newSingleThreadExecutor();
        }

        return executorSingleThread;
    }

}
