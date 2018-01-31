package pwojcik.pl.archcomponentstestproject.model.retrofit;

import pwojcik.pl.archcomponentstestproject.Utils.UrlUtil;
import pwojcik.pl.archcomponentstestproject.model.restObject.GithubUser;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wojci on 29.01.2018.
 */

public interface GithubRestInterface {

    @GET(UrlUtil.GET_USER)
    Call<GithubUser> getUser(@Path("user") String user);
}
