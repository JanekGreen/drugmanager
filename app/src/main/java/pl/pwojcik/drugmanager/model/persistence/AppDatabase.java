package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by pawel on 31.01.18.
 */

    @Database(entities = {DrugTime.class,DefinedTime.class,DrugDb.class},version = 3)
    public abstract class AppDatabase extends RoomDatabase {
        public abstract DrugTimeDao getDrugTimeDao();
        public abstract DrugDbDao getDrugDbDao();
        public abstract DefinedTimeDao getDefinedTimesDao();



    }


