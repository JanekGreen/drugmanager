package pl.pwojcik.drugmanager.repository;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugDbDao;

/**
 * Created by pawel on 19.02.18.
 */

public class DrugListRepositoryImpl implements DrugListRepository {

  private DefinedTimeDao definedTimeDao;
  private DrugDbDao drugDbDao;

    public DrugListRepositoryImpl(DefinedTimeDao definedTimeDao, DrugDbDao drugDbDao) {
        this.definedTimeDao = definedTimeDao;
        this.drugDbDao = drugDbDao;
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
}
