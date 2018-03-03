package pl.pwojcik.drugmanager.repository;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import java.io.File;
import java.util.HashMap;
import java.util.List;


import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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

    /**
     * rest calls
     */

    @Override
    public io.reactivex.Flowable<DrugDb> getDrugByEan(String ean) {


        io.reactivex.Observable<DrugDb> localSource = drugDbDao.geDrugByEanFromLocalDatabase("%" + ean + "%")
                .subscribeOn(Schedulers.io())
                .toObservable();

        io.reactivex.Observable<DrugDb> rest = drugRestInterface.getDrugByEan(ean)
                .subscribeOn(Schedulers.io())
                .map(drug -> Misc.getSpecificContainterInfo(drug, ean))
                .map(TypeConverter::makeDrugDatabaseEntity)
                .toObservable();

        return io.reactivex.Observable.concat(localSource, rest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .first(new DrugDb())
                .toFlowable();
    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }

    @Override
    public Flowable<Cursor> getNameSuggestionsForDrug(String name) {
        return drugRestInterface.getNameSuggestionForDrug(name)
                .subscribeOn(Schedulers.newThread())
                .map(this::createSuggestionsCursor)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<List<DrugDb>> getDrugsByName(String name) {
        return drugRestInterface.getDrugByName(name)
                .flatMap(Flowable::fromIterable)
                .subscribeOn(Schedulers.newThread())
                .map(TypeConverter::makeDrugDatabaseEntity)
                .doOnNext(drugDb -> drugDb.setPackQuantity(""))
                .take(10)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .toFlowable();
    }

    public Observable<File> downloadFileByUrl(String url) {
        return drugRestInterface.downloadFileByUrl(url)
                .subscribeOn(Schedulers.newThread())
                .map(Misc::downloadFile)
                .observeOn(AndroidSchedulers.mainThread());
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
                .toObservable()
                .subscribeOn(Schedulers.io())
                .flatMap(list -> Observable.fromIterable(list)
                        .map(definedTime -> definedTime.getName() + " - " + definedTime.getTime()))
                .toList()
                .toMaybe()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<List<String>> getAllDefinedTimesWithNamesAndRequestCodeId(int requestCode) {
        return definedTimeDao.getAll()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .flatMap(list -> Observable.fromIterable(list)
                        .filter(definedTime -> definedTime.getRequestCode() == requestCode)
                        .map(definedTime -> definedTime.getName() + " - " + definedTime.getTime()))
                .toList()
                .toMaybe()
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

    public Maybe<List<DrugDb>> getAll() {
        return drugDbDao.getAll()
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
    public Single<List<DrugTime>> removeDrugTimes(List<DrugTime> drugTimes) {
        return Single.just(drugTimes)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(drugTimes1 -> drugTimeDao.removeDrugTimes(drugTimes1))
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public void restoreDrugTimes(List<DrugTime> drugTimes) {
        Single.just(drugTimes)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(drugTimes1 -> drugTimeDao.restoreDrugTimes(drugTimes1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void removeDrugDb(DrugDb drugDb) {
        Single.just(drugDb)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(drug -> drugDbDao.deleteDrug(drug))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    @Override
    public Single<DrugDb> restoreDrugDb(DrugDb drugDb) {
        return Single.just(drugDb)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(drug -> drugDbDao.insertDrug(drug))
                .observeOn(AndroidSchedulers.mainThread());


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
    public Maybe<List<DrugTime>> getDrugTimesForDrug(long drugId) {
        return drugTimeDao.getDrugTimesForDrug(drugId)
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
    public Observable<List<DrugTime>> saveNewDrugTimeData(HashMap<Long, DrugTime> selectedIds, DrugDb drugDb) {

        return Observable.just(drugDb)
                .subscribeOn(Schedulers.io())
                .map(drugDb1 -> drugDbDao.insertDrug(drugDb1))
                .doOnNext(drugId -> drugTimeDao.removeDrugTimesForDrugDb(drugId))
                .flatMap(drugId -> Observable.fromIterable(selectedIds.values())
                        .subscribeOn(Schedulers.io())
                        .doOnNext(drugTime -> {
                            drugTime.setDrugId(drugId);
                        })
                        .toList()
                        .doOnSuccess(list -> drugTimeDao.insertDrugTime(list))
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable());

        /*        return io.reactivex.Observable.just(drugDb)
                .subscribeOn(Schedulers.io())
                .flatMap(drug -> io.reactivex.Observable.just(drugDbDao.insertDrug(drugDb)))
                .zipWith(io.reactivex.Observable.just(selectedIds.values()), (drugId, drugTimes) -> {
                    drugTimes.forEach(drugTime -> drugTime.setDrugId(drugId));
                    return drugTimes;
                })
                .doOnNext(drugTimes -> {
                    drugTimeDao.removeDrugTimesForDrugDb(drugDb.getId());
                    drugTimeDao.insertDrugTime(new ArrayList<>(drugTimes));
                });*/
        /*return Observable.fromIterable(selectedIds.values())
                .subscribeOn(Schedulers.io())
                .doOnNext(drugTime -> drugTime.setDrugId(drugDb.getId()))
                .toList()
                .toObservable();*/
    }

    private Cursor createSuggestionsCursor(List<String> list){
        MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});
        for (int i=0; i< list.size(); i++){
            cursor.addRow(new String[]{String.valueOf(i),list.get(i)});
        }
        return cursor;
    }
}

