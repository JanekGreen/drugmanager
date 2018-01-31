package pwojcik.pl.archcomponentstestproject.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import pwojcik.pl.archcomponentstestproject.model.restObject.GitHubUser;
import pwojcik.pl.archcomponentstestproject.model.retrofit.GithubRestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubRestRepositoryImpl implements GithubRestRepository{
    private GithubRestInterface githubRestInterface;
    private MutableLiveData<GitHubUser> data;

    public GitHubRestRepositoryImpl(GithubRestInterface githubRestInterface) {
        this.githubRestInterface = githubRestInterface;
        data = new MutableLiveData<>();
    }

    @Override
    public LiveData<GitHubUser> getUser(String user) {

        Call<GitHubUser> response = githubRestInterface.getUser(user);
        response.enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
                if(response.isSuccessful()) {
                    data.setValue(response.body());
                } else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {
            }
        });
        return data;
    }

    @Override
    public LiveData<GitHubUser> getData() {
        return data;
    }
}
