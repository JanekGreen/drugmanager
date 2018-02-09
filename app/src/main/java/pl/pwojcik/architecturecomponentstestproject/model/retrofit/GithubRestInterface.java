package pl.pwojcik.architecturecomponentstestproject.model.retrofit;

import io.reactivex.Maybe;
import pl.pwojcik.architecturecomponentstestproject.utils.UrlUtil;
import pl.pwojcik.architecturecomponentstestproject.model.restEntity.GithubUser;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wojci on 29.01.2018.
 */

public interface GithubRestInterface {

    @GET(UrlUtil.GET_USER)
    Maybe<GithubUser> getUser(@Path("user") String user);
}
