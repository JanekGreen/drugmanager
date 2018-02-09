package pl.pwojcik.drugmanager;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import pl.pwojcik.drugmanager.model.persistence.AppDatabase;

/**
 * Created by pawel on 01.02.18.
 */

public class DrugmanagerApplication extends Application {
    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room
                    .databaseBuilder(context, AppDatabase.class, "githubDb")
                    .build();
        }

        return db;
    }

}
