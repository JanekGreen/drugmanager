package pl.pwojcik.drugmanager.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
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
import pl.pwojcik.drugmanager.utils.Misc;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugRepostioryImpl implements DrugRepository {

    private DrugRestInterface drugRestInterface;
    private DrugTimeDao drugTimeDao;
    private DrugDbDao drugDbDao;
    private final DefinedTimeDao definedTimeDao;

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


        io.reactivex.Observable<Drug> localSource = drugDbDao.geDrugByEanFromLocalDatabase("%" + ean + "%")
                .subscribeOn(Schedulers.io())
                .toObservable()
                .map(TypeConverter::makeDrugFromDatabaseEntity);

        io.reactivex.Observable<Drug> rest = drugRestInterface.getDrugByEan(ean)
                .subscribeOn(Schedulers.io())
                .toObservable();

        return io.reactivex.Observable.concat(localSource,rest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .first(new Drug())
                .toFlowable();

        /*return drugDbDao.geDrugByEanFromLocalDatabase("%" + ean + "%")
                .map(TypeConverter::makeDrugFromDatabaseEntity)
                .subscribeOn(Schedulers.io())
                .isEmpty()
                .toFlowable()
                .flatMap(notInDatabase ->
                        notInDatabase ? drugRestInterface.getDrugByEan(ean) : Flowable
                                .error(new Throwable("Lek jest w bazie")))
                .map(drug -> Misc.getSpecificContainterInfo(drug, ean))
                .observeOn(AndroidSchedulers.mainThread());*/
    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }


    /**
     * ---------------------------------------------------------------------------------------
     * DefinedTimesEntity methods
     **/

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

    @Override
    public Maybe<List<DefinedTime>> getDefinedTimes() {
        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<List<String>> getAllDefinedTimesWithNames() {

        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .flatMap(definedTimes ->
                        Maybe.just(definedTimes.stream()
                                .map(definedTime -> definedTime.getName() + " - " + definedTime.getTime())
                                .collect(Collectors.toList()))
                )
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<List<String>> getAllDefinedTimesWithNamesAndRequestCodeId(int requestCode) {
        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .flatMap(definedTimes ->
                        Maybe.just(definedTimes.stream()
                                .filter(definedTime -> definedTime.getRequestCode() == requestCode)
                                .map(definedTime -> definedTime.getName() + " - " + definedTime.getTime())
                                .collect(Collectors.toList()))
                )
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<List<DefinedTime>> getDefinedTimesForDrug(long id) {
        return definedTimeDao.getDefinedTimesForDrug(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Long> getDefinedTimeIdForName(String name) {
        return definedTimeDao.getDefinedTimeIdForName(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Maybe<DefinedTime> insertDefineTime(DefinedTime definedTime) {
        return Maybe.just(definedTime)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(definedTime1 -> definedTimeDao.insertDefinedTime(definedTime))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<DefinedTime> removeDefinedTime(DefinedTime definedTime) {
        return Maybe.just(definedTime)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(definedTime1 -> {
                    definedTimeDao.removeDefinedTime(definedTime);

                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * ---------------------------------------------------------------------------------------
     * DrugDb entity methods
     **/

    @Override
    public Maybe<List<DrugDb>> getDrugsForTime(String timeName) {
        return drugDbDao.getDrugsForTime(timeName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<DrugDb> getDrugDbForId(long id) {
        return drugDbDao.getDrugDbForId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * ---------------------------------------------------------------------------------------
     * DrugTime entity methods
     **/
    @Override
    public void removeDrugTime(long definedTimeId, long drugId) {
        io.reactivex.Observable.just(drugTimeDao)
                .subscribeOn(Schedulers.io())
                .doOnNext(drugTimeDao1 -> drugTimeDao1.removeDrugTime(drugId, definedTimeId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void restoreDrugTimeItem(DrugTime drugTime) {
        io.reactivex.Observable.just(drugTime)
                .subscribeOn(Schedulers.io())
                .doOnNext(dt_ -> drugTimeDao.insertDrugTime(dt_))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public Maybe<DrugTime> getDrugTime(long drugId, long definedTimeId) {
        return drugTimeDao.getDrugTimeForDrug(drugId, definedTimeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<HashMap<Long, DrugTime>> getSelectedTimeIdsForDrug(long id) {
        return drugTimeDao.getDrugTimesForDrug(id)
                .subscribeOn(Schedulers.io())
                .map(drugTimes -> {
                    HashMap<Long, DrugTime> result = new HashMap<>();
                    for (DrugTime dt : drugTimes) {
                        result.put(dt.getTime_id(), dt);
                    }

                    return result;
                })
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
                    drugTimeDao.removeDrugTimesForDrugDb(drugDb.getId());
                    drugTimeDao.insertDrugTime(new ArrayList<>(drugTimes));
                });
    }
}

