package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by pawel on 15.02.18.
 */

@Dao
public interface DrugTimeDao {

    @Query("SELECT * from drug_time")
    Maybe<List<DrugTime>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDrugTime(DrugTime drugTime);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDrugTime(List<DrugTime> drugTimes);
    @Query("DELETE from drug_time where drug_id =:drugId and time_id= :drugTimeId")
    void removeDrugTime(long drugId, long drugTimeId);
    @Query("SELECT * from drug_time where drug_id=:drugId and time_id=:timeId")
    Maybe<DrugTime> getDrugTimeForDrug(long drugId, long timeId);


}
