package pl.pwojcik.architecturecomponentstestproject.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import pl.pwojcik.architecturecomponentstestproject.TemplateApplication;
import pl.pwojcik.architecturecomponentstestproject.model.restEntity.GithubUser;
import pl.pwojcik.architecturecomponentstestproject.model.retrofit.GitHubRestService;
import pl.pwojcik.architecturecomponentstestproject.repository.GitHubRestRepositoryImpl;
import pl.pwojcik.architecturecomponentstestproject.repository.GithubRestRepository;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubUserViewModel extends AndroidViewModel {
    private GithubRestRepository githubRestRepository;

    public GitHubUserViewModel(@NonNull Application application) {
        super(application);
        githubRestRepository = new GitHubRestRepositoryImpl(GitHubRestService.getGithubService(),
                TemplateApplication.getInstance(application.getApplicationContext()).GithubUserDbDao());
    }


    public LiveData<GithubUser> loadUser(String user) {
        return githubRestRepository.getUser(user);
    }

    public void deleteAll(){
        githubRestRepository.deleteAll();
    }
    public LiveData<GithubUser> getData() {
        return githubRestRepository.getData();
    }
}
