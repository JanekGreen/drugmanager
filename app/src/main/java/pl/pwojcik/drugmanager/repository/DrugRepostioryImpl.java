package pl.pwojcik.drugmanager.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.AppDatabase;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.persistence.DrugTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DrugDbDao;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.retrofit.DrugRestInterface;
import pl.pwojcik.drugmanager.utils.EAN13Validator;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugRepostioryImpl implements DrugRepository {


    private final DefinedTimeDao definedTimeDao;
    private DrugRestInterface drugRestInterface;
    private DrugTimeDao drugTimeDao;
    private DrugDbDao drugDbDao;
    private String currentEan = null;


    public DrugRepostioryImpl(DrugRestInterface drugRestInterface,
                              DrugTimeDao drugTimeDao,
                              DrugDbDao drugDbDao, DefinedTimeDao definedTimeDao) {

        this.drugRestInterface = drugRestInterface;
        this.drugTimeDao = drugTimeDao;
        this.drugDbDao = drugDbDao;
        this.definedTimeDao = definedTimeDao;
    }

    @Override
    public io.reactivex.Flowable<Drug> getDrugByEan(String ean) {
        if (isNewBarcodeScanned(ean) && isEanValid(ean)) {

            Flowable<Drug> result = drugRestInterface.getDrugByEan(ean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            currentEan = ean;

            return result;
        }

        return io.reactivex.Flowable.empty();

    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }

    @Override
    public Single<List<DefinedTime>> getDefinedTimes() {
        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void saveDrugTimeData(HashSet<Long> selectedIds, DrugDb drugDb) {
        Maybe.just(drugDb)
                .subscribeOn(Schedulers.io())
                .flatMap(
                        drugDb1 -> {
                            if (isDrugInLocalDatabase(drugDb1.getName()))
                                return Maybe.just(drugDbDao.getDrugIdForName(drugDb1.getName()));
                            else
                                return Maybe.just(drugDbDao.insertDrug(drugDb1));
                        })
                .zipWith(Maybe.just(selectedIds), (aLong, longs) -> {
                    List<DrugTime> result = new ArrayList<>();
                    for (long selectedId : longs) {
                        result.add(new DrugTime(aLong, selectedId));
                    }
                    return result;
                })
                .subscribe(drugTimes -> drugTimeDao.insertDrugTime(drugTimes)
                        , Throwable::printStackTrace);
    }

    private boolean isNewBarcodeScanned(String ean) {

        return currentEan == null || !ean.equals(currentEan);
    }

    private boolean isEanValid(String ean) {
        EAN13Validator ean13Validator = new EAN13Validator(ean);
        return ean13Validator.isValid();
    }

    boolean isDrugInLocalDatabase(String name) {
        return drugDbDao.drugCountInLocalDatabase(name) > 0;
    }

}


