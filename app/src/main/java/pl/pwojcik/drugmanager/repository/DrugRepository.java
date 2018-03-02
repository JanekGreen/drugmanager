package pl.pwojcik.drugmanager.repository;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.restEntity.Drug;

/**
 * Created by pawel on 10.02.18.
 */

public interface DrugRepository {
    io.reactivex.Flowable<DrugDb> getDrugByEan(String ean);
    List<Drug> getDrugListByName(String name);

    Maybe<List<DefinedTime>> getDefinedTimes();
    Maybe<Long> getDefinedTimeIdForName(String name);
    Maybe<List<String>> getAllDefinedTimesWithNames();
    Maybe<List<String>> getAllDefinedTimesWithNamesAndRequestCodeId(int requestCode);
    Maybe<List<DefinedTime>> getDefinedTimesForDrug(long id);
    Maybe<DefinedTime> insertDefineTime(DefinedTime definedTime);
    Maybe<DefinedTime> removeDefinedTime(DefinedTime definedTime);


    Maybe<List<DefinedTime>> updateSaveAlarms(Context context);
    Maybe<List<DrugDb>> getDrugsForTime(String timeName);
    Maybe<DrugDb> getDrugDbForId(long id);


    void removeDrugTime(long definedTimeId, long drugId);
    Single<List<DrugTime>> removeDrugTimes(List<DrugTime> drugTimes);
    void restoreDrugTimes(List<DrugTime> drugTimes);
    void removeDrugDb(DrugDb drugDb);
    Single<DrugDb> restoreDrugDb(DrugDb drugDb);

    void restoreDrugTimeItem(DrugTime drugTime);
    Maybe<DrugTime> getDrugTime(long drugId, long definedTimeId);
    Maybe<List<DrugTime>> getDrugTimesForDrug(long drugId);
    Maybe<HashMap<Long, DrugTime>> getSelectedTimeIdsForDrug(long id);
    Observable<List<DrugTime>> saveNewDrugTimeData(HashMap<Long,DrugTime> selectedIds, DrugDb drugDb);

    Maybe<List<DrugDb>> getAll();
    Observable<File> downloadFileByUrl(String url);
}
