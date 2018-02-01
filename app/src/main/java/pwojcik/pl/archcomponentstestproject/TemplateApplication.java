package pwojcik.pl.archcomponentstestproject;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pwojcik.pl.archcomponentstestproject.model.dbEntity.AppDatabase;

/**
 * Created by pawel on 01.02.18.
 */

public class TemplateApplication extends Application {
    private static AppDatabase db;
    private static ExecutorService executor;

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newSingleThreadExecutor();
    }

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room
                    .databaseBuilder(context, AppDatabase.class, "githubDb")
                    .build();
        }

        return db;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }
}
