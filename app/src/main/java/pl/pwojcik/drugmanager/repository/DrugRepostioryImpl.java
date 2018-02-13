package pl.pwojcik.drugmanager.repository;

import java.util.List;
import java.util.Objects;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.model.retrofit.DrugRestInterface;
import pl.pwojcik.drugmanager.utils.EAN13Validator;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugRepostioryImpl implements DrugRepository {

    private DrugRestInterface drugRestInterface;
    private String currentEan =null;



    public DrugRepostioryImpl(DrugRestInterface drugRestInterface) {
        this.drugRestInterface = drugRestInterface;
    }

    @Override
    public io.reactivex.Observable<Drug> getDrugByEan(String ean) {
        if(isNewBarcodeScanned(ean) && isEanValid(ean)) {

            Observable<Drug> result = drugRestInterface.getDrugByEan(ean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            currentEan = ean;

            return result;
        }

        return io.reactivex.Observable.empty();

    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }


    private boolean isNewBarcodeScanned(String ean) {

        return currentEan == null || !ean.equals(currentEan);
    }

    private boolean isEanValid(String ean){
        EAN13Validator ean13Validator = new EAN13Validator(ean);
        return ean13Validator .isValid();
    }

}


