package pwojcik.pl.archcomponentstestproject.model.retrofit;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import pwojcik.pl.archcomponentstestproject.Utils.UrlUtil;
import pwojcik.pl.archcomponentstestproject.model.restEntity.GithubUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wojci on 29.01.2018.
 */

public interface GithubRestInterface {

    @GET(UrlUtil.GET_USER)
    Flowable<GithubUser> getUser(@Path("user") String user);
}
