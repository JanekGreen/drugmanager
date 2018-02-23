package pl.pwojcik.drugmanager.ui.druginfo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.List;

import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DrugDb;
import pl.pwojcik.drugmanager.repository.DrugRepository;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;
import pl.pwojcik.drugmanager.retrofit.DrugRestService;

/**
 * Created by pawel on 23.02.18.
 */

public class DrugInfoViewModel extends AndroidViewModel {

    private DrugRepository drugRepository;
    private MutableLiveData<DrugDb> drugDbMutableLiveData;
    private MutableLiveData<List<DefinedTime>> definedTimeMutableLiveData;

    public DrugInfoViewModel(@NonNull Application application) {
        super(application);

        drugDbMutableLiveData = new MutableLiveData<>();
        definedTimeMutableLiveData = new MutableLiveData<>();

        drugRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService(),
                DrugmanagerApplication.getDbInstance(application).getDrugTimeDao(), DrugmanagerApplication.getDbInstance(application).getDrugDbDao(),
                DrugmanagerApplication.getDbInstance(application).getDefinedTimesDao());
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

    public LiveData<DrugDb> getDrugDbData(){
        return drugDbMutableLiveData;
    }

    public MutableLiveData<List<DefinedTime>> getDefinedTimeData(long id) {
        drugRepository.getDefinedTimesForDrug(id)
        .subscribe(definedTimes -> definedTimeMutableLiveData.setValue(definedTimes),
                throwable -> {
                    Toast.makeText(getApplication().getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG)
                            .show();
                });
        return definedTimeMutableLiveData;
    }
    public MutableLiveData<List<DefinedTime>> getDefinedTimeData() {

        return definedTimeMutableLiveData;
    }
}
