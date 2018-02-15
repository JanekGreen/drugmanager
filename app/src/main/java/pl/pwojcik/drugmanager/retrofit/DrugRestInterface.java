package pl.pwojcik.drugmanager.retrofit;

import io.reactivex.Flowable;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.utils.Constants;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wojci on 29.01.2018.
 */

public interface DrugRestInterface {

    @GET(Constants.GET_DRUG)
    Flowable<Drug> getDrugByEan(@Path("ean") String ean);
}
