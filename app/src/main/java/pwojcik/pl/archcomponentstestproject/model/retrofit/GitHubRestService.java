package pwojcik.pl.archcomponentstestproject.model.retrofit;

import pwojcik.pl.archcomponentstestproject.Utils.UrlUtil;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubRestService {
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
    public static GithubRestInterface getGithubService(){
        return GitHubRestService.getClient().create(GithubRestInterface.class);
    }
}
