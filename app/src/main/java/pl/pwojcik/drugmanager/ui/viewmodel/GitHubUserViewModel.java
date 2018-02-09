package pl.pwojcik.drugmanager.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import pl.pwojcik.drugmanager.DrugmanagerApplication;
import pl.pwojcik.drugmanager.model.restEntity.GithubUser;
import pl.pwojcik.drugmanager.model.retrofit.GitHubRestService;
import pl.pwojcik.drugmanager.repository.GitHubRestRepositoryImpl;
import pl.pwojcik.drugmanager.repository.GithubRestRepository;

/**
 * Created by wojci on 29.01.2018.
 */

public class GitHubUserViewModel extends AndroidViewModel {
    private GithubRestRepository githubRestRepository;

    public GitHubUserViewModel(@NonNull Application application) {
        super(application);
        githubRestRepository = new GitHubRestRepositoryImpl(GitHubRestService.getGithubService(),
                DrugmanagerApplication.getInstance(application.getApplicationContext()).GithubUserDbDao());
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
