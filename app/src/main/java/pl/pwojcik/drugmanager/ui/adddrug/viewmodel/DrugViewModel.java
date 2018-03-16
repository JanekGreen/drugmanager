package pl.pwojcik.drugmanager.ui.adddrug.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimesDays;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.retrofit.DrugRestService;
import pl.pwojcik.drugmanager.repository.DrugRepository;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugViewModel extends AndroidViewModel {

    private DrugRepository drugRepository;
    private MutableLiveData<List<DefinedTime>> definedTimeLiveData = new MutableLiveData<>();
    private MutableLiveData<HashMap<Long, DrugTime>> selectedTimesIds;
    private MutableLiveData<DrugDb> drugDbMutableLiveData;

    public DrugViewModel(@NonNull Application application) {
        super(application);
        drugRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
                DrugmanagerApplication.getDbInstance(application).getDrugTimeDao(), DrugmanagerApplication.getDbInstance(application).getDrugDbDao(),
                DrugmanagerApplication.getDbInstance(application).getDefinedTimesDao(), DrugmanagerApplication.getDbInstance(application).getDefinedTimesDaysDao());

        selectedTimesIds = new MutableLiveData<>();
        selectedTimesIds.setValue(new HashMap<>());
        drugDbMutableLiveData = new MutableLiveData<>();
    }

    public Flowable<DrugDb> getDrugByEan(String ean) {

        return drugRepository.getDrugByEan(ean);

    }


    public MutableLiveData<List<DefinedTime>> getDefinedTimesData() {
        drugRepository
                .getDefinedTimes()
                .subscribe(definedTimes -> definedTimeLiveData.setValue(definedTimes),
                        throwable -> {
                            System.err.println(throwable.getMessage());
                            Toast.makeText(getApplication().getApplicationContext(),
                                    throwable.getMessage(), Toast.LENGTH_LONG).show();

                        });
        return definedTimeLiveData;
    }

    public void addSelectedTimeForDrug(long definedTimeId, boolean isSelected) {
        if (!isSelected) {
            if (selectedTimesIds.getValue() != null && selectedTimesIds.getValue().containsKey(definedTimeId)) {
                HashMap<Long, DrugTime> tmp = selectedTimesIds.getValue();
                tmp.remove(definedTimeId);
                selectedTimesIds.setValue(tmp);
            }
        } else {
            DrugTime drugTime = new DrugTime();
            drugTime.setTime_id(definedTimeId);

            assert selectedTimesIds.getValue() != null;
            HashMap<Long, DrugTime> tmp = selectedTimesIds.getValue();
            tmp.put(definedTimeId, drugTime);
            getSelectedTimesIds().setValue(tmp);

        }
    }

    public io.reactivex.Observable<List<DrugTime>> saveDrugTimeData() {
        return drugRepository
                .saveNewDrugTimeData(selectedTimesIds.getValue(),
                        drugDbMutableLiveData.getValue());

    }

    public MutableLiveData<HashMap<Long, DrugTime>> getSelectedTimesIds() {
        return selectedTimesIds;
    }

    public void getSelectedTimesIds(long drugDbId) {
        drugRepository.getSelectedTimeIdsForDrug(drugDbId)
                .subscribe(hm -> selectedTimesIds.setValue(hm));
    }

    public Single<Boolean> shouldPromptForSave(){
        DrugDb drugDb = drugDbMutableLiveData.getValue();
        if(drugDb!=null && drugDb.getId()>0 &&  selectedTimesIds.getValue()!= null){
           return drugRepository.shouldPromptForSave(drugDb.getId(), selectedTimesIds.getValue());

        }

        return Single.just(false);
    }

    public Maybe<List<DefinedTime>> updateOrSetAlarms(Context context) {
        return drugRepository.updateSaveAlarms(context);
    }

    public Maybe<DefinedTime> insertDefinedTime(DefinedTime definedTime,List<DefinedTimesDays> definedTimesDays) {
        return drugRepository.insertDefineTime(definedTime,definedTimesDays);

    }

    public Maybe<DefinedTime> removeDefinedTime(DefinedTime definedTime,List<DefinedTimesDays> definedTimesDays) {
        return drugRepository.removeDefinedTime(definedTime, definedTimesDays);
    }

    public LiveData<DrugDb> getDrugDbData(long id) {
        drugRepository.getDrugDbForId(id)
                .subscribe(drugDb -> drugDbMutableLiveData.setValue(drugDb),
                        throwable -> {
                            Toast.makeText(getApplication().getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        });

        return drugDbMutableLiveData;
    }

    public MutableLiveData<DrugDb> getDrugDbData() {
        return drugDbMutableLiveData;
    }

    public  Observable<File> downloadFileByUrl(String url){
        return drugRepository.downloadFileByUrl(url);
    }

    public Flowable<Cursor> getNameSuggestionsForDrug(String name){
        return drugRepository.getNameSuggestionsForDrug(name);

    }
    public Flowable<List<DrugDb>> getDrugsForName(String name){
        return drugRepository.getDrugsByName(name);
    }

    public Single<DrugDb> saveDrug(DrugDb drugDb){
        return drugRepository.saveDrug(drugDb);
    }

    public Observable<List<DefinedTimesDays>> saveNewDefinedTimesData(DefinedTime definedTime, List<Integer> activeDays) {
        return drugRepository.saveNewDefinedTimesData(definedTime,activeDays);
    }

    public Maybe<List<DefinedTimesDays>> getDefinedTimesDays(long id){
        return drugRepository.getDefinedTimeDaysForDefinedTime(id);
    }


}





