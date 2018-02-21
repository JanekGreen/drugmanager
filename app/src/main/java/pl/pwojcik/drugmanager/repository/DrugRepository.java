package pl.pwojcik.drugmanager.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import io.reactivex.Maybe;
import io.reactivex.Single;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.restEntity.Drug;

/**
 * Created by pawel on 10.02.18.
 */

public interface DrugRepository {
    io.reactivex.Flowable<Drug> getDrugByEan(String ean);
    List<Drug> getDrugListByName(String name);
    Maybe<List<DefinedTime>> getDefinedTimes();
    io.reactivex.Observable<Collection<DrugTime>> saveNewDrugTimeData(HashMap<Long,DrugTime> selectedIds, DrugDb drugDb);
    Maybe<List<DefinedTime>> updateSaveAlarms(Context context);
}
