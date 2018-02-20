package pl.pwojcik.drugmanager.repository;

import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugDbDao;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.persistence.DrugTimeDao;

/**
 * Created by pawel on 19.02.18.
 */

public class DrugListRepositoryImpl implements DrugListRepository {

  private DefinedTimeDao definedTimeDao;
  private DrugDbDao drugDbDao;
  private DrugTimeDao drugTimeDao;

    public DrugListRepositoryImpl(DefinedTimeDao definedTimeDao, DrugDbDao drugDbDao
    ,DrugTimeDao drugTimeDao){
        this.definedTimeDao = definedTimeDao;
        this.drugDbDao = drugDbDao;
        this.drugTimeDao = drugTimeDao;
    }

    @Override
    public Maybe<List<String>> getAllDefinedTimes() {

        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .flatMap(definedTimes ->
                    Maybe.just(definedTimes.stream()
                            .map(definedTime ->definedTime.getName()+" - "+definedTime.getTime())
                            .collect(Collectors.toList()))
                )
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Maybe<List<DrugDb>> getDrugsForTime(String timeName) {
        return drugDbDao.getDrugsForTime(timeName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Maybe<Long> getDefinedTimeIdForName(String name){
        return definedTimeDao.getDefinedTimeIdForName(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void removeDrugTime(long definedTimeId, long drugId) {
        io.reactivex.Observable.just(drugTimeDao)
                .subscribeOn(Schedulers.io())
                .doOnNext(drugTimeDao1 -> drugTimeDao1.removeDrugTime(drugId,definedTimeId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void restoreDrugTimeItem(long drugId, long drugTimeId) {
        DrugTime dt = new DrugTime(drugId,drugTimeId);
        io.reactivex.Observable.just(dt)
                .subscribeOn(Schedulers.io())
                .doOnNext(dt_ ->drugTimeDao.insertDrugTime(dt_))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }
}
