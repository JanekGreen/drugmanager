package pl.pwojcik.drugmanager.ui.adddrug.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;

import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
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
    private MutableLiveData<Drug> drugLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DefinedTime>> definedTimeLiveData = new MutableLiveData<>();
    private HashSet<Long> selectedTimesIds;

    public DrugViewModel(@NonNull Application application) {
        super(application);
        drugRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
                DrugmanagerApplication.getInstance(application).getDrugTimeDao(), DrugmanagerApplication.getInstance(application).getDrugDbDao(),
                DrugmanagerApplication.getInstance(application).getDefinedTimesDao());

        selectedTimesIds = new HashSet<>();
        drugLiveData.setValue(null);
    }

    public void getDrugByEan(String ean) {

        drugRepository.getDrugByEan(ean)
                .subscribe(drug ->
                                drugLiveData.setValue(drug),
                        e -> {
                            System.err.println(e.getMessage());
                            Toast.makeText(getApplication().getApplicationContext(),
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                );
    }

    public MutableLiveData<Drug> getDrugData() {
        return drugLiveData;
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

    public void addSelectedTimeForDrug(long definedTimeId, boolean isSelected){
        if(!isSelected){
            if(selectedTimesIds.contains(definedTimeId)) {
                selectedTimesIds.remove(definedTimeId);
            }
        } else {
            selectedTimesIds.add(definedTimeId);
        }
    }

    public void saveDrugTimeData(){
        drugRepository
                .saveDrugTimeData(selectedTimesIds,
                        TypeConverter.makeDrugDatabaseEntity(drugLiveData.getValue()));

    }

}
