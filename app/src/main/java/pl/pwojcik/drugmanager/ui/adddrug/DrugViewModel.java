package pl.pwojcik.drugmanager.ui.adddrug;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.model.retrofit.DrugRestService;
import pl.pwojcik.drugmanager.repository.DrugRepository;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugViewModel extends AndroidViewModel {

    private DrugRepository drugRepository;
    private MutableLiveData<Drug> drugLiveData = new MutableLiveData<>();

    public DrugViewModel(@NonNull Application application) {
        super(application);
        drugRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService());
        drugLiveData.setValue(new Drug());
    }

    void getDrugByEan(String ean) {

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

    public MutableLiveData<Drug> getData() {
        return drugLiveData;
    }
}
