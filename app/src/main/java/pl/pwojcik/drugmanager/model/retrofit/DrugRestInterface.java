package pl.pwojcik.drugmanager.model.retrofit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.utils.UrlUtil;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wojci on 29.01.2018.
 */

public interface DrugRestInterface {

    @GET(UrlUtil.GET_DRUG)
    Observable<Drug> getDrugByEan(@Path("ean") String ean);
}
