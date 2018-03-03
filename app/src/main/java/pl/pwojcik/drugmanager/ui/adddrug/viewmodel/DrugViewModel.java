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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.model.persistence.DrugTime;
import pl.pwojcik.drugmanager.model.persistence.TypeConverter;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
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
                DrugmanagerApplication.getDbInstance(application).getDefinedTimesDao());

        selectedTimesIds = new MutableLiveData<>();
        selectedTimesIds.setValue(new HashMap<>());
        drugDbMutableLiveData = new MutableLiveData<>();
    }

    public void getDrugByEan(String ean) {

        drugRepository.getDrugByEan(ean)
                .subscribe(drug ->
                        drugDbMutableLiveData.setValue(drug),
                        e -> {
                            System.err.println(e.getMessage());
                            Toast.makeText(getApplication().getApplicationContext(),
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                );
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

    public MutableLiveData<HashMap<Long, DrugTime>> getSelectedTimesIds(long drugDbId) {
        drugRepository.getSelectedTimeIdsForDrug(drugDbId)
                .subscribe(hm -> selectedTimesIds.setValue(hm));
        return selectedTimesIds;
    }

    public Maybe<List<DefinedTime>> updateOrSetAlarms(Context context) {
        return drugRepository.updateSaveAlarms(context);
    }

    public Maybe<DefinedTime> insertDefinedTime(DefinedTime definedTime) {
        return drugRepository.insertDefineTime(definedTime);

    }

    public Maybe<DefinedTime> removeDefinedTime(DefinedTime definedTime) {
        return drugRepository.removeDefinedTime(definedTime);
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
}



