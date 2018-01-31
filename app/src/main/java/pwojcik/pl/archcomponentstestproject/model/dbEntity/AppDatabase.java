package pwojcik.pl.archcomponentstestproject.model.dbEntity;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by pawel on 31.01.18.
 */

    @Database(entities = {GithubUserDb.class},version = 1)
    public abstract class AppDatabase extends RoomDatabase {
        public abstract GithubUserDao GithubUserDbDao();

    }


