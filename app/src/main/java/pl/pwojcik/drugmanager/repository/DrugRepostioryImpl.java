package pl.pwojcik.drugmanager.repository;

import java.util.List;
import java.util.Objects;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.model.retrofit.DrugRestInterface;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugRepostioryImpl implements DrugRepository {

    DrugRestInterface drugRestInterface;


    public DrugRepostioryImpl(DrugRestInterface drugRestInterface) {
        this.drugRestInterface = drugRestInterface;
    }

    @Override
    public Maybe<Drug> getDrugByEan(String ean) {

        return drugRestInterface.getDrugByEan(ean)
            .subscribeOn(Schedulers.io())
            .filter(Objects::nonNull)
            .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }
}
