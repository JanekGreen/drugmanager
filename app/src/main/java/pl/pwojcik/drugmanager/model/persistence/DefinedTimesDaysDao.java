package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by pawel on 12.03.18.
 */

@Dao
public interface DefinedTimesDaysDao {
    @Query("SELECT * from defined_time_days")
    Maybe<List<DefinedTimesDays>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDefineTimeDays(List<DefinedTimesDays> definedTimesDays);

    @Delete
    void removeDefinedTimesDays(List<DefinedTimesDays> definedTimesDays);

    @Query("SELECT * from defined_time_days where time_id=:definedTimeId")
    Maybe<List<DefinedTimesDays>> getDefinedTimeDaysForDefinedTime(long definedTimeId);

    @Query("DELETE from defined_time_days where time_id = :id")
    void removeDefinedTimesDaysForDefinedTime(long id);

}
