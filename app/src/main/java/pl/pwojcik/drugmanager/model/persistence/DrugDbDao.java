package pl.pwojcik.drugmanager.model.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by pawel on 15.02.18.
 */

@Dao
public interface DrugDbDao {
    @Query("SELECT * from drugs")
    Maybe<List<DrugDb>> getAll();

    @Query("SELECT drugs.name,drugs.pack_quantity,drugs.id,drugs.active_substance,drugs.dosage,drugs.usage_type,drugs.producer,drugs.leaflet, drugs.characteristics from drugs left join drug_time on drug_time.drug_id = drugs.id " +
            "left join defined_times on drug_time.time_id =defined_times.id where defined_times.name = :timeName")
    Maybe<List<DrugDb>> getDrugsForTime(String timeName);

    @Query("SELECT * from drugs where id in (:ids)")
    Maybe<List<DrugDb>> getDrugsForIds(List<Long> ids);

    @Query("SELECT * from drugs where id =:id")
    Maybe<DrugDb> getDrugDbForId(long id);

    @Query("SELECT count (*) from drugs where drugs.name = :name")
    int drugCountInLocalDatabase(String name);

    @Query("SELECT id from drugs where drugs.name = :name")
    Long getDrugIdForName(String name);

    @Query("SELECT * from drugs where note like :ean")
    Maybe<DrugDb> geDrugByEanFromLocalDatabase(String ean);

    @Query("SELECT MAX(id) as id from drugs")
    Maybe<Long> getNextId();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
     long insertDrug(DrugDb drugDb);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertDrugs(DrugDb... drugDb);

    @Delete
    void deleteDrug(DrugDb drugDb);
}
