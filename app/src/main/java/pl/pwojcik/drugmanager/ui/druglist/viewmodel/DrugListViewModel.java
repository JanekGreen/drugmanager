package pl.pwojcik.drugmanager.ui.druglist.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Observable;

import io.reactivex.Maybe;
import io.reactivex.Single;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.repository.DrugRepository;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;
import pl.pwojcik.drugmanager.retrofit.DrugRestService;

/**
 * Created by pawel on 19.02.18.
 */

public class DrugListViewModel extends AndroidViewModel {

    private DrugRepository drugListRepository;
    private MutableLiveData<List<String>> drugListLiveData;

    public DrugListViewModel(@NonNull Application application) {
        super(application);


        drugListRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
        DrugmanagerApplication.getDbInstance(application).getDrugTimeDao(), DrugmanagerApplication.getDbInstance(application).getDrugDbDao(),
                DrugmanagerApplication.getDbInstance(application).getDefinedTimesDao());

        drugListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<String>> getDefinedTimes() {

          drugListRepository
                .getAllDefinedTimesWithNames()
          .subscribe(list -> drugListLiveData.postValue(list));
          return drugListLiveData;
    }

    public Maybe<List<DrugDb>> getDrugsForTime(String timeName){
       return drugListRepository.getDrugsForTime(timeName);

    }
    public Maybe<Long> getIdDefinedTimeIdForName(String name){
        return drugListRepository.getDefinedTimeIdForName(name);
    }

    public void removeDrugTime(long definedTimeId, long drugId) {
        drugListRepository.removeDrugTime(definedTimeId,drugId);
    }

    public void removeDrugTime(DrugTime drugTime) {
        drugListRepository.removeDrugTime(drugTime);
    }

    public void restoreDrugTime(DrugTime drugTime) {
        drugListRepository.restoreDrugTimeItem(drugTime);
    }
    public Maybe<DrugTime> getDrugTime(long drugId, long definedTimeId) {
       return drugListRepository.getDrugTime(drugId,definedTimeId);
    }

    public Maybe<List<String>> getDefinedTimeForRequestCode(int requestCode) {
        return  drugListRepository
                .getAllDefinedTimesWithNamesAndRequestCodeId(requestCode);

    }
    public Maybe<DefinedTime> getDefinedTimeForRequestCodeAsDefTime(int requestCode) {
        return  drugListRepository
                .getDefinedTimes()
                .flatMap(list -> io.reactivex.Observable.fromIterable(list)
                .filter(definedTime -> definedTime.getRequestCode() == requestCode)
                .singleElement())
                .doOnSuccess(definedTime -> System.out.println("Defined Time time "+definedTime.getTime()));

    }
    public Maybe<List<DrugDb>> getAllDrugs(){
        return drugListRepository.getAll();
    }

    public Maybe<List<DrugTime>> getDrugTimesForDrug(long drugId){
       return drugListRepository.getDrugTimesForDrug(drugId);
    }

    public void removeDrug(DrugDb drugDb){
        drugListRepository.removeDrugDb(drugDb);
    }
    public Single<DrugDb> restoreDrug(DrugDb drugDb){
       return drugListRepository.restoreDrugDb(drugDb);
    }
    public Single<List<DrugTime>> removeDrugTimes(List<DrugTime> drugTimes){
        return drugListRepository.removeDrugTimes(drugTimes);
    }
    public void restoreDrugTimes(List<DrugTime> drugTimes){
         drugListRepository.restoreDrugTimes(drugTimes);
    }
    public Maybe<DefinedTime> insertDefinedTime(DefinedTime definedTime){

        return drugListRepository.insertDefineTime(definedTime);
    }
}
