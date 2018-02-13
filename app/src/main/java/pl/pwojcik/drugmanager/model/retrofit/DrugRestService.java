package pl.pwojcik.drugmanager.model.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.pwojcik.drugmanager.utils.Constants;
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

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static DrugRestInterface getDrugRestService(){
        return DrugRestService.getClient().create(DrugRestInterface.class);
    }
}
