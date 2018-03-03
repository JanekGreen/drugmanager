package pl.pwojcik.drugmanager.retrofit;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import pl.pwojcik.drugmanager.model.restEntity.Drug;
import pl.pwojcik.drugmanager.utils.Constants;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by wojci on 29.01.2018.
 */

public interface DrugRestInterface {

    @GET(Constants.GET_DRUG_BY_EAN)
    Flowable<Drug> getDrugByEan(@Path("ean") String ean);

    @GET(Constants.GET_DRUG_BY_NAME)
    Flowable<List<Drug>> getDrugByName(@Path("name") String name);

    @GET(Constants.GET_NAME_SUGGESTION)
    Flowable<List<String>> getNameSuggestionForDrug(@Path("name") String name);

    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFileByUrl(@Url String fileUrl);
}
