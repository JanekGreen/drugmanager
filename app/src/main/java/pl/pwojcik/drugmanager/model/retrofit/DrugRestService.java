package pl.pwojcik.drugmanager.model.retrofit;

import pl.pwojcik.drugmanager.utils.UrlUtil;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wojci on 29.01.2018.
 */

public class DrugRestService {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(UrlUtil.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static DrugRestInterface getDrugRestService(){
        return DrugRestService.getClient().create(DrugRestInterface.class);
    }
}
