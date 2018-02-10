package pl.pwojcik.drugmanager.ui.adddrug;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.model.retrofit.DrugRestService;
import pl.pwojcik.drugmanager.repository.DrugRepository;
import pl.pwojcik.drugmanager.repository.DrugRepostioryImpl;

/**
 * Created by pawel on 10.02.18.
 */

public class AddByBarcodeViewModel extends AndroidViewModel{

    private DrugRepository drugRepository;
    private MutableLiveData<Drug> drugLiveData = new MutableLiveData<>();

    public AddByBarcodeViewModel(@NonNull Application application) {
        super(application);
        drugRepository = new DrugRepostioryImpl(DrugRestService.getDrugRestService());
    }

    public LiveData<Drug> getDrugByEan(String ean){
        drugRepository.getDrugByEan(ean)
                .subscribe(drug ->
                                drugLiveData.setValue(drug),
                                e-> System.err.println(e.getMessage()));
         return drugLiveData;
    }
    public LiveData<Drug> getData(){
        return drugLiveData;
    }
}
