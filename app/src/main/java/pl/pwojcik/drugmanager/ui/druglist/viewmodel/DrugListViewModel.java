package pl.pwojcik.drugmanager.ui.druglist.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.repository.DrugListRepository;
import pl.pwojcik.drugmanager.repository.DrugListRepositoryImpl;

/**
 * Created by pawel on 19.02.18.
 */

public class DrugListViewModel extends AndroidViewModel {

    private DrugListRepository drugListRepository;
    private MutableLiveData<List<String>> drugListLiveData;

    public DrugListViewModel(@NonNull Application application) {
        super(application);

        drugListRepository = new DrugListRepositoryImpl(DrugmanagerApplication
                .getDbInstance(application)
                .getDefinedTimesDao(), DrugmanagerApplication
                .getDbInstance(application)
                .getDrugDbDao());
        drugListLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<List<String>> getDefinedTimes() {

          drugListRepository
                .getAllDefinedTimes()

          .subscribe(list -> drugListLiveData.postValue(list));

          return drugListLiveData;
    }

    public Maybe<List<DrugDb>> getDrugsForTime(String timeName){
       return drugListRepository.getDrugsForTime(timeName);

    }
}
