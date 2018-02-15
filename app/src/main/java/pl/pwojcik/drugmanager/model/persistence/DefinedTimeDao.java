package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by pawel on 15.02.18.
 */

@Dao
public interface DefinedTimeDao {
    @Query("SELECT * from defined_times")
    Single<List<DefinedTime>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDefinedTime(DefinedTime definedTime);
    @Insert
    void insertDefinedTimes(DefinedTime... definedTimes);
    @Delete
    void deleteDefinedTime(DefinedTime definedTime);

}
