package pl.pwojcik.drugmanager.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.persistence.DrugTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DrugDbDao;
import pl.pwojcik.drugmanager.model.persistence.TypeConverter;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.notification.alarm.AlarmHelper;
import pl.pwojcik.drugmanager.retrofit.DrugRestInterface;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugRepostioryImpl implements DrugRepository {


    private final DefinedTimeDao definedTimeDao;
    private DrugRestInterface drugRestInterface;
    private DrugTimeDao drugTimeDao;
    private DrugDbDao drugDbDao;


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

        return drugDbDao.geDrugByEanFromLocalDatabase("%" + ean + "%")
                .map(TypeConverter::makeDrugFromDatabaseEntity)
                .subscribeOn(Schedulers.io())
                .isEmpty()
                .toFlowable()
                .flatMap(notInDatabase ->
                        notInDatabase ? drugRestInterface.getDrugByEan(ean) : Flowable
                                .error(new Throwable("Lek jest w bazie")))
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }

    @Override
    public Maybe<List<DefinedTime>> getDefinedTimes() {
        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public io.reactivex.Observable<Collection<DrugTime>> saveNewDrugTimeData(HashMap<Long, DrugTime> selectedIds, DrugDb drugDb) {


        return io.reactivex.Observable.just(drugDb)
                .subscribeOn(Schedulers.io())
                .flatMap(drug -> io.reactivex.Observable.just(drugDbDao.insertDrug(drugDb)))
                .zipWith(io.reactivex.Observable.just(selectedIds.values()), (drugId, drugTimes) -> {
                    drugTimes.forEach(drugTime -> drugTime.setDrugId(drugId));
                    return drugTimes;
                })
                .doOnNext(drugTimes -> {
                    drugTimeDao.insertDrugTime(new ArrayList<>(drugTimes));
                });
    }

    public Maybe<List<DefinedTime>> updateSaveAlarms(Context context) {

        return definedTimeDao
                .getDefinedTimesForActiveDrugs()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(listDefinedTimes -> {
                    AlarmHelper alarmHelper = new AlarmHelper(context);
                    // najpierw usuwam wszystkie
                    definedTimeDao.getAll()
                            .subscribeOn(Schedulers.newThread())
                            .doOnSuccess(alarmHelper::cancelAllAlarms)
                            .subscribe(definedTimes -> {
                                // teraz dopiero zapisuje
                                alarmHelper.setOrUpdateAlarms(listDefinedTimes);
                            },
                                    throwable -> System.out.println(throwable.getMessage()));
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

}


