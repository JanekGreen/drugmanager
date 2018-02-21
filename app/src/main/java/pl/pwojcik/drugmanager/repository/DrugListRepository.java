package pl.pwojcik.drugmanager.repository;

import java.util.List;

import io.reactivex.Maybe;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;

/**
 * Created by pawel on 19.02.18.
 */

public interface DrugListRepository {
    Maybe<List<String>> getAllDefinedTimes();
    Maybe<List<DrugDb>> getDrugsForTime(String timeName);
    Maybe<Long> getDefinedTimeIdForName(String name);
    void removeDrugTime(long definedTimeId, long drugId);
    void restoreDrugTimeItem(DrugTime drugTime);
    Maybe<DrugTime> getDrugTime(long drugId, long definedTimeId);
}
