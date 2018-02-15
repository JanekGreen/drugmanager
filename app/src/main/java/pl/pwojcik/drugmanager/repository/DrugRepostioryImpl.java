package pl.pwojcik.drugmanager.repository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.pwojcik.drugmanager.model.persistence.DrugTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DefinedTime;
import pl.pwojcik.drugmanager.model.persistence.DefinedTimeDao;
import pl.pwojcik.drugmanager.model.persistence.DrugDbDao;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.retrofit.DrugRestInterface;
import pl.pwojcik.drugmanager.utils.EAN13Validator;

/**
 * Created by pawel on 10.02.18.
 */

public class DrugRepostioryImpl implements DrugRepository {


    private final DefinedTimeDao definedTimeDao;
    private DrugRestInterface drugRestInterface;
    private DrugTimeDao drugTimeDao;
    private DrugDbDao drugDbDao;
    private String currentEan =null;


    public DrugRepostioryImpl(DrugRestInterface drugRestInterface,
                              DrugTimeDao drugTimeDao,
                              DrugDbDao drugDbDao, DefinedTimeDao definedTimeDao) {

        this.drugRestInterface = drugRestInterface;
        this.drugTimeDao = drugTimeDao;
        this.drugDbDao = drugDbDao;
        this.definedTimeDao = definedTimeDao;
    }

    @Override
    public io.reactivex.Flowable<Drug> getDrugByEan(String ean) {
        if(isNewBarcodeScanned(ean) && isEanValid(ean)) {

            Flowable<Drug> result = drugRestInterface.getDrugByEan(ean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            currentEan = ean;

            return result;
        }

        return io.reactivex.Flowable.empty();

    }

    @Override
    public List<Drug> getDrugListByName(String name) {
        return null;
    }

    @Override
    public Single<List<DefinedTime>> getDefinedTimes() {
        return definedTimeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private boolean isNewBarcodeScanned(String ean) {

        return currentEan == null || !ean.equals(currentEan);
    }

    private boolean isEanValid(String ean){
        EAN13Validator ean13Validator = new EAN13Validator(ean);
        return ean13Validator .isValid();
    }

}


