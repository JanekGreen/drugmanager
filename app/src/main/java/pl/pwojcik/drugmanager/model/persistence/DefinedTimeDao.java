package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by pawel on 15.02.18.
 */

@Dao
public interface DefinedTimeDao {
    @Query("SELECT * from defined_times")
    Maybe<List<DefinedTime>> getAll();

    @Query("SELECT count (*) as size from defined_times")
    Maybe<Integer> getDefinedTimesCount();

    @Query("select * from defined_times where id in (select distinct time_id from drug_time)")
    Maybe<List<DefinedTime>> getDefinedTimesForActiveDrugs();

    @Query("SELECT id from defined_times where defined_times.name = :name")
    Maybe<Long> getDefinedTimeIdForName(String name);

    @Query("SELECT * from defined_times  where id in(Select time_id from drug_time where drug_id =:id)")
    Maybe<List<DefinedTime>> getDefinedTimesForDrug(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDefinedTime(DefinedTime definedTime);

    @Insert
    void insertDefinedTimes(DefinedTime... definedTimes);

    @Delete
    void removeDefinedTime(DefinedTime definedTime);
}
