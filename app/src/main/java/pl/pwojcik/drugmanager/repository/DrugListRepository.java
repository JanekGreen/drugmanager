package pl.pwojcik.drugmanager.repository;

import java.util.List;

import io.reactivex.Maybe;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;

/**
 * Created by pawel on 19.02.18.
 */

public interface DrugListRepository {
    Maybe<List<String>> getAllDefinedTimes();
    Maybe<List<DrugDb>> getDrugsForTime(String timeName);
}
