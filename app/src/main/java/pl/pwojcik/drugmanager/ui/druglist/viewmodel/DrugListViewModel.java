package pl.pwojcik.drugmanager.ui.druglist.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
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

    public DrugListViewModel(@NonNull Application application) {
        super(application);


        drugListRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
        DrugmanagerApplication.getDbInstance(application).getDrugTimeDao(), DrugmanagerApplication.getDbInstance(application).getDrugDbDao(),
                DrugmanagerApplication.getDbInstance(application).getDefinedTimesDao(), DrugmanagerApplication.getDbInstance(application).getDefinedTimesDaysDao());

    }

    public Maybe<List<String>> getDefinedTimes() {
       //return LiveDataReactiveStreams.fromPublisher(drugListRepository.getAllDefinedTimesWithNames().toFlowable());
       return drugListRepository.getAllDefinedTimesWithNames();

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

    public io.reactivex.Observable<Integer> removeDrugTime(DrugTime drugTime) {
       return drugListRepository.removeDrugTime(drugTime);
    }

    public io.reactivex.Observable<DrugTime> restoreDrugTime(DrugTime drugTime) {
        return drugListRepository.restoreDrugTimeItem(drugTime);
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

    public Single<DrugDb> removeDrug(DrugDb drugDb){
        return drugListRepository.removeDrugDb(drugDb);
    }
    public Single<DrugDb> restoreDrug(DrugDb drugDb){
       return drugListRepository.restoreDrugDb(drugDb);
    }
    public Single<List<DrugTime>> removeDrugTimes(List<DrugTime> drugTimes){
        return drugListRepository.removeDrugTimes(drugTimes);
    }
    public Single<List<DrugTime>> restoreDrugTimes(List<DrugTime> drugTimes){
         return drugListRepository.restoreDrugTimes(drugTimes);
    }
    public Maybe<List<DefinedTime>> updateOrSetAlarms(Context context) {
        return drugListRepository.updateSaveAlarms(context);
    }
    public Maybe<List<DefinedTimesDays>> getDefinedTimesDays(long id){
        return drugListRepository.getDefinedTimeDaysForDefinedTime(id);
    }
}
