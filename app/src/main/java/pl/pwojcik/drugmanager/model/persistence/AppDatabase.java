package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by pawel on 31.01.18.
 */

    @Database(entities = {DrugTime.class,DefinedTime.class,DrugDb.class, DefinedTimesDays.class},version = 9)
    public abstract class AppDatabase extends RoomDatabase {
        public abstract DrugTimeDao getDrugTimeDao();
        public abstract DrugDbDao getDrugDbDao();
        public abstract DefinedTimeDao getDefinedTimesDao();
        public abstract DefinedTimesDaysDao getDefinedTimesDaysDao();

    }


