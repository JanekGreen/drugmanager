package pl.pwojcik.drugmanager.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;


import io.reactivex.Maybe;
import pl.pwojcik.drugmanager.model.restEntity.Drug;

/**
 * Created by pawel on 10.02.18.
 */

public interface DrugRepository {
    io.reactivex.Observable<Drug> getDrugByEan(String ean);
    List<Drug> getDrugListByName(String name);
}
