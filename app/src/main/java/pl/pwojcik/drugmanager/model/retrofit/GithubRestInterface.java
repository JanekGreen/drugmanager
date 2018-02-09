package pl.pwojcik.drugmanager.model.retrofit;

import io.reactivex.Maybe;
import pl.pwojcik.drugmanager.utils.UrlUtil;
import pl.pwojcik.drugmanager.model.restEntity.GithubUser;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wojci on 29.01.2018.
 */

public interface GithubRestInterface {

    @GET(UrlUtil.GET_USER)
    Maybe<GithubUser> getUser(@Path("user") String user);
}
